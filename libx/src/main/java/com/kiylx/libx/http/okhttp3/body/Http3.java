package com.kiylx.libx.http.okhttp3.body;

@SuppressWarnings("unused")
public class Http3 {

    public static class CHARSET {
        public static final String CHARSET_UTF8 = "UTF-8";
        public static final String CHARSET_GBK = "GBK";
        public static final String CHARSET_GB2312 = "GB2312";
        public static final String CHARSET_ISO8859_1 = "ISO-8859-1";
        public static final String CHARSET_UTF16 = "UTF-16";
        public static final String CHARSET_UTF32 = "UTF-32";
    }

    public static class ContentType {
        public static final String www_form_urlencoded = "application/x-www-form-urlencoded";
        public static final String form_data = "multipart/form-data";
        public static final String application_json = "application/json";
        public static final String application_json_utf8 = "application/json; charset=UTF8";
    }

    public static class MediaType {
        public static final String APPLICATION_JSON = "application/json";
        public static final String APPLICATION_JSON_UTF8 = "application/json; charset=UTF8";
        public static final String APPLICATION_XML = "application/xml";
        public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
        public static final String APPLICATION_STREAM = "application/octet-stream";

        public static final String MULTIPART_FORM_DATA = "multipart/form-data";


        public static final String Video_MPEG = "video/mpeg";
        public static final String Video_MP4 = "video/mp4";
        public static final String Video_AVI = "video/avi";
        public static final String Video_3GP = "video/3gp";

        public static final String TEXT_PLAIN = "text/plain";
        public static final String TEXT_HTML = "text/html";
        public static final String TEXT_XML = "text/xml";
        public static final String TEXT_CSV = "text/csv";

        public static final String IMAGE_BASIC = "image/*";
        public static final String IMAGE_JPEG = "image/jpeg";
        public static final String IMAGE_PNG = "image/png";
        public static final String IMAGE_GIF = "image/gif";
        public static final String IMAGE_BMP = "image/bmp";
        public static final String IMAGE_WEBP = "image/webp";
    }

    static class HttpMethod {
        public static final String GET = "GET";
        public static final String POST = "POST";
        public static final String PUT = "PUT";
        public static final String DELETE = "DELETE";
    }
}
