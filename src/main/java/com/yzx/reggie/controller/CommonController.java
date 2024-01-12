package com.yzx.reggie.controller;

import com.yzx.reggie.common.R;
import com.yzx.reggie.dto.DishDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MultipartFilter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传upload和下载download
 */
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    //这里必须要MultipartFile类的参数，file的参数名字也不能乱写
    public R<String> upload(MultipartFile file) {
        //接收到前端传来的图片文件，file实际是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info(file.toString());
        //将临时文件钻转存到指定位置,transferTo方法会判断传进来的是否是相对路径，如果是它就会用它的temp路径作为本次路径的父目录，然后将我们的路径一起转化为绝对路径，很难找
        //我们传入绝对路径没问题
        //先获取相对路径的绝对路径，将获取的绝对路径+文件名一起传进去就可以完美解决
        //这里存到./src/main/resources/cache
        //String path = "./src/main/resources/cache";
        //String destPath = new File(path).getAbsolutePath();
        //log.info(destPath);

        //文件位置basepath改成可配置类型，配在配置文件里，用@Value取


        //获取原始文件名
        String originalFileName = file.getOriginalFilename();//abc.jpg
        //动态截取原始文件的文件类型后缀
        String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
        //使用uuid重新生成文件名，防止文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix; //asdassdfa.jpg

        //File既可以代表目录又可以代表文件
        File dir = new File(basePath);//目录
        //判断当前目录是否存在
        if (!dir.exists()) {
            dir.mkdirs();//不存在自动创建
        }
        try {


            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /**
     * 文件下载，在upload完之后，前端立马调用会掉函数call到这里获取文件内容回显到前端展示
     *
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        log.info(name);
        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            //输出流，通过输出流将文件写回浏览器,这里的输出流不是我们自己new出来的，而是要通过response响应对象来获取输出流给浏览器
            //获取输出流对象
            ServletOutputStream outputStream = response.getOutputStream();
            //设置一下响应回去的是什么类型的文件
            response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            //每次读完放到bytes数组里去
            while ((len = fileInputStream.read(bytes)) != -1) {
                //通过输出流往浏览器写,从第一个写，写到len这么长
                outputStream.write(bytes, 0, len);
                outputStream.flush(); //刷新一下
            }
            //通过两个流的配合实现读写回传

            //写完后关闭资源
            outputStream.close();
            fileInputStream.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


}
