/*
��Ȩ������
��Դ��ϵ��Java���̱߳��ʵսָ�ϣ�����ƪ����һ�飨ISBN��978-7-121-31065-2�����³�֮Ϊ��ԭ�顱��������Դ�룬
���˽Ȿ����ĸ���ϸ�ڣ���ο�ԭ�顣
�������Ϊԭ�������˵��֮�ã����������κγ�ŵ����������֤�����棩��
���κ���ʽ��������֮���ֻ���ȫ������Ӫ������;�辭��Ȩ������ͬ�⡣
��������֮���ֻ���ȫ�����ڷ�Ӫ������;��Ҫ�ڴ����б�����������
�κζԱ�������޸����ڴ�������ע�͵���ʽע���޸��ˡ��޸�ʱ���Լ��޸����ݡ�
��������Դ�������ַ���أ�
https://github.com/Viscent/javamtia
http://www.broadview.com.cn/31065
*/
package io.github.viscent.mtia.ch2;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * ��Demo����ʹ��32λJava��������ܿ�����ԭ�Ӳ�����Ч��. <br>
 * ���б�DemoʱҲ����ָ�������������-client��
 *
 * @author Viscent Huang
 */
public class NonAtomicAssignmentDemo implements Runnable {
    static long value = 0;
    private final long valueToSet;

    public NonAtomicAssignmentDemo(long valueToSet) {
        this.valueToSet = valueToSet;
    }

    public static void main(String[] args) {
        // �߳�updateThread1��data����Ϊ0
        Thread updateThread1 = new Thread(new NonAtomicAssignmentDemo(0L));
        // �߳�updateThread2��data����Ϊ-1
        Thread updateThread2 = new Thread(new NonAtomicAssignmentDemo(-1L));
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);
        executorService.schedule(updateThread1, 2, TimeUnit.SECONDS);
        executorService.schedule(updateThread2, 2, TimeUnit.SECONDS);
        while (0 == value || -1 == value) {
            System.out.println("������˵�����ѭ�� ���ᱻ �˳���value Ҫô1 Ҫô0����ʵ�� ����");
        }
        System.err.printf("Unexpected data: %d(0x%016x)", value, value);
    }

    static class DummyOutputStream extends OutputStream {
        @Override
        public void write(int b) throws IOException {
            // ��ʵ�ʽ������
        }
    }

    @SuppressFBWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD",
            justification = "����Ϊ֮")
    @Override
    public void run() {
        for (; ; ) {
            value = valueToSet;
        }
    }
}