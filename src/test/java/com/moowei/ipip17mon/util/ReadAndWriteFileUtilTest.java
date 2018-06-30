package com.moowei.ipip17mon.util;

import com.moowei.ipip17mon.thirdparty.Locator;
import org.junit.Test;


import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * Created by blw on 2018/3/22 0022.
 */
public class ReadAndWriteFileUtilTest {

//    @Test
    public void getDataByURL() throws IOException {
        String outPutPath = "D:\\USER\\BLW\\Desktop\\Data warehouse\\IPIP\\test_Observer\\iphone_test.txt";
//        String URL = "https://user.ipip.net/download.php?";
        String URL = "https://user.ipip.net/download.php?";
        StringBuffer stringBuffer = new StringBuffer();
        byte[] bytesData = Locator.loadFromNet(URL);
        ByteBuffer buf = ByteBuffer.wrap(bytesData);
        write(outPutPath,buf);
//        WriteToFileUtil.bufferedWriteAndFileWriterTest(buf,outPutPath,"UTF-8");
    }
    private void write(String outPutPath,ByteBuffer byteBuffer) throws IOException {
        Charset charset = Charset.forName("UTF-8");
        CharsetDecoder decoder = charset.newDecoder();
        CharBuffer charBuffer = decoder.decode(byteBuffer.asReadOnlyBuffer());
        ByteBuffer writer = charset.encode(charBuffer);
//        String str = charBuffer.toString();
        File f = new File(outPutPath);
        // System.out.println(f.exists());
        FileChannel writeChannel = new FileOutputStream(f, false).getChannel();
        writeChannel.write(writer);
        writeChannel.close();
    }

    @Test
    public void lsf4jTest() {
//        Logger logger = LoggerFactory.getLogger(ReadAndWriteFileUtilTest.class);
//        logger.info("Hello World");
//        File file = new File(Thread.currentThread().getContextClassLoader().getResource("readtest").getFile());
//        System.out.println(file.getPath());
//        BufferedReader br = new BufferedReader(new FileReader(file));
//        String len = null;
//        if ((len=br.readLine())!=null){
//            System.out.println(len);
//        }

//        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("readtest");
//        StringBuffer out = new StringBuffer();
//        byte[] b = new byte[4096];
//        for (int n; (n = inputStream.read(b)) != -1;) {
//            out.append(new String(b, 0, n));
//        }
//        System.out.println(out.toString());
//        System.out.println(inputStream2.);
    }
}
