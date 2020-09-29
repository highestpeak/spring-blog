package com.highestpeak.springblog.constant;


/**
 * @author highestpeak
 * 只写在代码中需要的，即需要知道数据库列字段名的字段
 */
public class SqlTableConstant {

    public static class ArticleColName {
        public static final String ID = "id";
    }

    public static class ArticleTagColName {
        public static final String ARTICLE = "article";
        public static final String TAG = "tag";
    }

    public static class ArticleLocationColName {
        public static final String ARTICLE = "article";
        public static final String LOCATION_VALUE = "location_value";
        public static final String CREATE_TIME = "create_time";
        public static final String UPDATE_TIME = "update_time";
    }

    public static class RepoColName {
        public static final String REPO_URL = "repo_url";
        public static final String CREATE_TIME = "create_time";
        public static final String DESC_ARTICLE_ID = "article";
    }

    public static class TagColName {
        public static final String NAME = "name";
    }

}
