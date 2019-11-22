package com.orange.tpms.lib.driver.channel;

/**
 * 数据通道类型, 如UART,I2C,SPI等的通用接口
 * @author zeqiang
 */
public interface IChannel {

    public int open ();
    public int close ();
    public int write (byte[] in);
    public byte[] read ();

}
