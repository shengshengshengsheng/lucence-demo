package com.itheima.lucence;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;

/**
 * description:
 *
 * @author xuqiangsheng
 * @date 2020/11/27 10:44
 */
public class QueryIndexTest {

    private IndexSearcher indexSearcher;

    @Before
    public void init() throws Exception {
        Directory directory = FSDirectory.open(new File("E:\\learning\\lucence-demo\\files").toPath());
        IndexReader indexReader = DirectoryReader.open(directory);
        this.indexSearcher = new IndexSearcher(indexReader);
    }

    public IndexSearcher getIndexSearcher() {
        return indexSearcher;
    }

    /**
     * 使用Termquery查询
     *
     * @throws Exception 抛出异常
     */
    @Test
    public void testTermQuery() throws Exception {
        IndexSearcher indexSearcher = this.getIndexSearcher();
        //创建查询对象
        Query query = new TermQuery(new Term("fileContent", "lucene"));
        this.printResult(query, indexSearcher);
        //关闭indexreader
        indexSearcher.getIndexReader().close();
    }

    /**
     * 数值范围查询
     *
     * @author XuQiangsheng
     * @date 2020/11/27 10:51
     */
    @Test
    public void testRangeQuery() throws Exception {
        IndexSearcher indexSearcher = getIndexSearcher();
        Query query = LongPoint.newRangeQuery("fileSize", 0L, 100L);
        printResult(query, indexSearcher);
    }

    /**
     * 通过QueryParser也可以创建Query，QueryParser提供一个Parse方法，
     * 此方法可以直接根据查询语法来查询。
     * Query对象执行的查询语法可通过System.out.println(query);查询。
     *
     * @param
     * @return void
     * @author XuQiangsheng
     * @date 2020/11/27 10:52
     */
    @Test
    public void testQueryParser() throws Exception {
        IndexSearcher indexSearcher = getIndexSearcher();
        //创建queryparser对象
        //第一个参数默认搜索的域
        //第二个参数就是分析器对象
        QueryParser queryParser = new QueryParser("fileContent", new IKAnalyzer());
        Query query = queryParser.parse("Lucene是java开发的");
        //执行查询
        printResult(query, indexSearcher);
    }

    private void printResult(Query query, IndexSearcher indexSearcher) throws Exception {
        //执行查询
        TopDocs topDocs = indexSearcher.search(query, 10);
        //共查询到的document个数
        System.out.println("查询结果总数量：" + topDocs.totalHits);
        //遍历查询结果
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document document = indexSearcher.doc(scoreDoc.doc);
            System.out.println(document.get("fileName"));
            System.out.println(document.get("filePath"));
            System.out.println(document.get("fileSize"));
        }
    }
}
