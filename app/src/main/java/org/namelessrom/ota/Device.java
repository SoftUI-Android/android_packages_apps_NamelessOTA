/*
 * <!--
 *    Copyright (C) 2013 - 2015 The NamelessRom Project
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 * -->
 */

package org.namelessrom.ota;

import android.os.SystemProperties;

/**
 * Represents the device
 */
public class Device {
    public static final String UNKNOWN = "unknown";

    private static final Device sInstance = new Device();

    public final String name; // ro.product.device
    public final int date;    // ro.nameless.date

    private Device() {
        name = SystemProperties.get("ro.product.device", UNKNOWN);
        date = SystemProperties.getInt("ro.nameless.date", -1);
    }

    public static Device get() {
        return sInstance;
    }

    public String toString() {
        return String.format("Name: %s", name);
    }

}
