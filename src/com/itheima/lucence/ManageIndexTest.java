package com.itheima.lucence;

import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;

/**
 * description:管理所引
 *
 * @author xuqiangsheng
 * @date 2020/11/27 10:13
 */
public class ManageIndexTest {

    private IndexWriter indexWriter;

    @Before
    public void init() throws Exception{
        //索引库存放路径
        Directory directory = FSDirectory.open(new File("E:\\learning\\lucence-demo\\index").toPath());
        IndexWriterConfig conf = new IndexWriterConfig(new IKAnalyzer());
        //创建一个IndexWriter对象
        this.indexWriter = new IndexWriter(directory, conf);
    }

    public IndexWriter getIndexWriter() {
        return indexWriter;
    }

    /**
     * 添加文档到索引库
     * @author XuQiangsheng
     * @date 2020/11/27 10:14
    */
    @Test
    public void addDocument() throws Exception{
        IndexWriter indexWriter = getIndexWriter();
        //创建一个document对象
        Document doc = new Document();
        //向document对象中添加域
        //不同的document可以有不同的域，相同的document可以有相同的域
        doc.add(new TextField("fileName","新添加的文档", Field.Store.YES));
        doc.add(new TextField("fileContent","新添加的内容", Field.Store.NO));
        //LongPoint创建fileSize索引
        doc.add(new LongPoint("fileSize",100L));
        //StoredField存储fileSize数据
        doc.add(new StoredField("fileSize",100L));
        //不需要创建索引的就使用StoredField直接存储，跳过创建索引操作
        doc.add(new StoredField("filePath","E:\\learning\\lucence-demo\\files"));
        //添加文档到索引库
        indexWriter.addDocument(doc);
        //关闭indexWriter
        indexWriter.close();
    }

    /**
     *  删除全部索引
     * @author XuQiangsheng
     * @date 2020/11/27 10:30
    */
    @Test
    public void deleteAllIndex() throws Exception{
        IndexWriter indexWriter = getIndexWriter();
        //删除全部索引
        indexWriter.deleteAll();
        indexWriter.close();
    }

    /**
     * 根据查询条件执行删除索引操作
     * @author XuQiangsheng
     * @date 2020/11/27 10:33
    */
    @Test
    public void deleteIndexByQuery() throws Exception{
        IndexWriter indexWriter = getIndexWriter();
        //创建查询条件
        Query query = new TermQuery(new Term("fileName","apache"));
        //执行删除
        indexWriter.deleteDocuments(query);
        //关闭indexWriter
        indexWriter.close();
    }

    /**
     * 修改索引库
     *      修改的实质是先删除索引再添加新的索引
     *          需要更新的域在索引库中原本有两个索引
     *          删除原有的两个索引，然后增加更新的这个索引，最终域中只会有现在增加的这个索引
     * @author XuQiangsheng
     * @date 2020/11/27 10:34
    */
    @Test
    public void updateIndex() throws Exception{
        IndexWriter indexWriter = getIndexWriter();
        //创建一个Document对象
        Document document = new Document();
        //向document对象中添加域。
        //不同的document可以有不同的域，同一个document可以有相同的域。
        document.add(new TextField("fileName", "要更新的文档", Field.Store.YES));
        document.add(new TextField("fileContent", " Lucene 简介 Lucene 是一个基于 Java 的全文信息检索工具包," +
                "它不是一个完整的搜索应用程序,而是为你的应用程序提供索引和搜索功能。",
                Field.Store.YES));
        //先删除域fileContent内容为java的索引，然后添加更新的document
        indexWriter.updateDocument(new Term("fileContent", "java"), document);
        //关闭indexWriter
        indexWriter.close();
    }
}
