package com.fanql.netlibrary.request;

import java.io.File;
import java.util.List;

public class FileRequest extends Request {

    public File file;
    public List<FileInput> mFileInputList;

    public static class FileInput {
        public String key;
        public String filename;
        public File file;

        public FileInput(String name, String filename, File file) {
            this.key = name;
            this.filename = filename;
            this.file = file;
        }

    }


}
