package com.ultrasafe;

import java.io.File;

public interface StatusUpdater {
    void update(boolean unlocked, File file, int entryCount);
}
