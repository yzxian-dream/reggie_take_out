package com.yzx.reggie;

import org.junit.jupiter.api.Test;

public class UploadFileTest {
    @Test
    public void test(){
        String filename = "asdasd.jpg";
        String suffix = filename.substring(filename.lastIndexOf("."));
        System.out.println(suffix);
    }
}
