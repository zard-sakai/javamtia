/*
授权声明：
本源码系《Java多线程编程实战指南（核心篇）》一书（ISBN：978-7-121-31065-2，以下称之为“原书”）的配套源码，
欲了解本代码的更多细节，请参考原书。
本代码仅为原书的配套说明之用，并不附带任何承诺（如质量保证和收益）。
以任何形式将本代码之部分或者全部用于营利性用途需经版权人书面同意。
将本代码之部分或者全部用于非营利性用途需要在代码中保留本声明。
任何对本代码的修改需在代码中以注释的形式注明修改人、修改时间以及修改内容。
本代码可以从以下网址下载：
https://github.com/Viscent/javamtia
http://www.broadview.com.cn/31065
*/
package io.github.viscent.mtia.ch5.case03;

import io.github.viscent.mtia.ch4.case02.AbstractLogReader;
import io.github.viscent.mtia.ch4.case02.RecordSet;

import java.io.InputStream;
import java.util.concurrent.Exchanger;

public class ExchangerBasedLogReaderThread extends AbstractLogReader {
  private final Exchanger<RecordSet> exchanger;
  private volatile RecordSet nextToFill;
  private RecordSet consumedBatch;

  public ExchangerBasedLogReaderThread(InputStream in, int inputBufferSize,
      int batchSize) {
    super(in, inputBufferSize, batchSize);
    exchanger = new Exchanger<RecordSet>();
    nextToFill = new RecordSet(batchSize);
    consumedBatch = new RecordSet(batchSize);
  }

  @Override
  protected RecordSet getNextToFill() {
    return nextToFill;
  }

  @Override
  protected void publish(RecordSet recordSet) throws InterruptedException {
      // 提供一个 填充完毕的缓冲区 来 获取 一个 空的缓冲区 继续 填充数据
    nextToFill = exchanger.exchange(recordSet);
  }

  @Override
  protected RecordSet nextBatch() throws InterruptedException {
      // 提供一个 消费完成的缓冲区 来 获取 一个 待消费的缓冲区 继续 消费数据
    consumedBatch = exchanger.exchange(consumedBatch);
    if (consumedBatch.isEmpty()) {
      consumedBatch = null;
    }
    return consumedBatch;
  }
}