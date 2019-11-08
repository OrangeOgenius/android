package com.orange.tpms.lib.driver.channel;

import com.orange.tpms.lib.hardware.HardwareApp;

/**
 * 空通道
 */
public class EmptyChannel implements IChannel {
    @Override
    public int open() {
        return 0;
    }

    @Override
    public int close() {
        return 0;
    }

    @Override
    public int write(byte[] in) {
        return 0;
    }

    @Override
    public byte[] read() {
        return new byte[0];
    }
}
