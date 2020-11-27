package com.itheima.lucence;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.util.Objects;

/**
 * description:创建 和 查询索引
 *
 * @author xuqiangsheng
 * @date 2020/11/27 8:21
 */
public class CreateIndexTest {
    /**
     * 创建索引步骤
     * 1、创建一个Director对象，指定索引库保存的位置。
     * 2、基于Directory对象创建一个IndexWriter对象
     * 3、读取磁盘上的文件，对应每个文件创建一个文档对象。
     * 4、向文档对象中添加域
     * 5、把文档对象写入索引库
     * 6、关闭indexwriter对象
     *
     * @author XuQiangsheng
     * @date 2020/11/27 9:21
     */
    @Test
    public void testCreateIndex() throws Exception {
        //1、创建一个Directory对象，指定索引库保存的位置。
        Directory directory = FSDirectory.open(new File("E:\\learning\\lucence-demo\\index").toPath());
        //new IndexWriterConfig() 默认使用的是标准分析器new StandardAnalyzer()
        //IndexWriterConfig conf = new IndexWriterConfig();
        //此处可以自定义分析器 如使用ik分析器
        IndexWriterConfig conf = new IndexWriterConfig(new IKAnalyzer());
        //2、基于Directory对象创建一个IndexWriter对象
        IndexWriter indexWriter = new IndexWriter(directory, conf);
        //3、读取磁盘上的文件，对应每个文件创建一个文档对象。
        File dir = new File("E:\\learning\\lucence-demo\\files");
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            //文件名
            String fileName = file.getName();
            //文件内容
            String fileContent = FileUtils.readFileToString(file, "utf-8");
            //文件路径
            String filePath = file.getPath();
            //文件大小
            long fileSize = FileUtils.sizeOf(file);
            //创建文件名域
            /*
             * 三个参数的含义
             *      String name 域的名称
             *      String value 域的内容
             *      Store store 是否存储
             */
            Field fileNameField = new TextField("fileName", fileName, Field.Store.YES);
            //文件内容域
            Field fileContentField = new TextField("fileContent", fileContent, Field.Store.YES);
            //文件路径域
            Field filePathField = new TextField("filePath", filePath, Field.Store.YES);
            //文件大小域
            Field fileSizeField = new TextField("fileSize", fileSize + "", Field.Store.YES);
            //创建document对象
            Document document = new Document();
            //向文档对象中添加域
            document.add(fileNameField);
            document.add(fileContentField);
            document.add(filePathField);
            document.add(fileSizeField);
            //4.把文档对象写入索引库
            indexWriter.addDocument(document);
        }
        //5.关闭indexWriter对象
        indexWriter.close();
    }

    /**
     * 查询索引
     * 第一步：创建一个Directory对象，也就是索引库存放的位置。
     * 第二步：创建一个indexReader对象，需要指定Directory对象。
     * 第三步：创建一个indexsearcher对象，需要指定IndexReader对象
     * 第四步：创建一个TermQuery对象，指定查询的域和查询的关键词。
     * 第五步：执行查询。
     * 第六步：返回查询结果。遍历查询结果并输出。
     * 第七步：关闭IndexReader对象
     *
     * @author XuQiangsheng
     * @date 2020/11/27 9:25
     */
    @Test
    public void testQueryIndex() throws Exception {
        //第一步：创建一个Directory对象，也就是索引库存放的位置。
        Directory directory = FSDirectory.open(new File("E:\\learning\\lucence-demo\\index").toPath());
        //第二步：创建一个indexReader对象，需要指定Directory对象。
        IndexReader indexReader = DirectoryReader.open(directory);
        //第三步：创建一个indexsearcher对象，用于查询索引，需要指定IndexReader对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //第四步：创建一个TermQuery对象，指定查询的域和查询的关键词。
        /*
         *两个参数
         *      String fld 要查询的field的名称
         *      String text 要查询的关键词(内容)
         */
        Term term = new Term("fileName", "apache");
        Query query = new TermQuery(term);
        //第五步：执行查询，返回查询结果
        /*
         *两个参数
         *      Query query 查询对象
         *      int n 查询结果返回的最大值
         */
        TopDocs topDocs = indexSearcher.search(query, 10);
        //查询结果的总条数
        System.out.println("查询结果的总条数:" + topDocs.totalHits);
        //第六步：遍历查询结果并输出。
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            //scoreDoc.doc就是文档对象的id
            int docId = scoreDoc.doc;
            //根据docId找到document对象
            Document document = indexSearcher.doc(docId);
            //打印document对象中的属性
            System.out.println("文件名：" + document.get("fileName"));
            ///不打印文件的内容，太长了
//            System.out.println(document.get("fileContent"));
            System.out.println("文件大小：" + document.get("fileSize"));
            System.out.println("文件路径：" + document.get("filePath"));
            System.out.println("---------------------寂寞的分割线---------------------------");
        }
    }

    /**
     * 查看标准分析器的分词效果
     *      单字分词：就是按照中文一个字一个字地进行分词。如：“我爱中国”，
     *      效果：“我”、“爱”、“中”、“国”。
     * @author XuQiangsheng
     * @date 2020/11/27 9:49
    */
    @Test
    public void testTokenStream() throws Exception{
        //创建标准分析器对象
        Analyzer analyzer = new StandardAnalyzer();
        //获得TokenStream对象
        /*
         * 两个参数
         *      String fieldName 域名称
         *      String text 域的内容
         */
        //这里的文本可以从前文的文档的读取
        TokenStream tokenStream = analyzer.tokenStream("test","The Spring Framework provides a comprehensive programming and configuration model.");
        //添加一个引用 可以获得每个关键词
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        //添加一个偏移量的引用，记录了关键词的开始位置和结束为止
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
        //将指针调整到列表的头部
        tokenStream.reset();
        //遍历关键词列表，通过incrementToken方法判断列表是否结束
        while (tokenStream.incrementToken()){
            //关键词的起始位置
            System.out.println("start->" + offsetAttribute.startOffset());
            //取出关键词
            System.out.println(charTermAttribute);
            //关键词的结束位置
            System.out.println("end->" + offsetAttribute.endOffset());
        }
        //关闭tokenStream
        tokenStream.close();
    }
}
