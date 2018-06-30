package com.moowei.ipip17mon.util;
/**
 * @author blw
 */

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class WriteToFileUtil{
    public void outputStreamTest(int count, String str) {
        File f = new File("f:test1.txt");
        OutputStream os = null;
        try {
            os = new FileOutputStream(f);
            for (int i = 0; i < count; i++) {
                os.write(str.getBytes());
            }
            os.flush();
            System.out.println("file's long:" + f.length());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *2 按字节缓冲写入 BufferedOutputStream
     *
     * @param count 写入循环次数
     * @param str 写入字符串
     */
    public void bufferedOutputTest(int count, String str) {
        File f = new File("f:test2.txt");
        BufferedOutputStream bos = null;
        try {
            OutputStream os = new FileOutputStream(f);
            bos = new BufferedOutputStream(os);
            for (int i = 0; i < count; i++) {
                bos.write(str.getBytes());
            }
            bos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *3 按字符写入 FileWriter
     *
     * @param count 写入循环次数
     * @param str 写入字符串
     */
    public void fileWriteTest(int count, String str) {
        File f = new File("f:test.txt");
        Writer writer = null;
        try {
            writer = new FileWriter(f);
            for (int i = 0; i < count; i++) {
                writer.write(str);
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *4 按字符缓冲写入 BufferedWriter
     *
     * @param count 写入循环次数
     * @param str 写入字符串
     */
    public void bufferedWriteTest(int count, String str) {
        File f = new File("f:test3.txt");
        OutputStreamWriter writer = null;
        BufferedWriter bw = null;
        try {
            OutputStream os = new FileOutputStream(f);
            writer = new OutputStreamWriter(os);
            bw = new BufferedWriter(writer);
            for (int i = 0; i < count; i++) {
                bw.write(str);
            }
            bw.flush();
            if(f.exists()){
                f.delete();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *5 按字符缓冲写入 BufferedWriter and BufferedOutputStream
     *
     * @param count 写入循环次数
     * @param str 写入字符串
     */
    public void bufferedWriteAndBufferedOutputStreamTest(int count, String str) {
        File f = new File("f:test4.txt");
        BufferedOutputStream bos=null;
        OutputStreamWriter writer = null;
        BufferedWriter bw = null;
        try {
            OutputStream os = new FileOutputStream(f);
            bos=new BufferedOutputStream(os);
            writer = new OutputStreamWriter(bos);
            bw = new BufferedWriter(writer);
            for (int i = 0; i < count; i++) {
                bw.write(str);
            }
            bw.flush();
            if(f.exists()){
                f.delete();
                System.out.println("delete---");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *6 按字符缓冲写入 BufferedWriter and FileWriter
     *
     * @param count 写入循环次数
     * @param str 写入字符串
     */
    public void bufferedWriteAndFileWriterTest(int count, String str) {
        File f = new File("f:test5.txt");
        FileWriter fw=null;
        BufferedWriter bw = null;
        try {
            fw=new FileWriter(f);
            bw = new BufferedWriter(fw);
            for (int i = 0; i < count; i++) {
                bw.write(str);
            }
            bw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
                if(f.exists()){
                    f.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 字符缓冲写入
     * @param stringBuffer 要写入的数据
     * @param filePath 要写入的文件地址
     * @param encoding 写文件时的文件编码
     */
    public static void bufferedWriteAndFileWriterTest(StringBuffer stringBuffer,String filePath,String encoding) {
        File file = new File(filePath);
//        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
//            bufferedWriter = new BufferedWriter( new FileWriter(file));
            bufferedWriter.write(stringBuffer.toString());
            bufferedWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(bufferedWriter);
        }
    }



}

