package com.sun.j3d.utils.timer;

/** @deprecated */
public class J3DTimer
{
  private static final long resolution = 1000L;

  public static long getValue()
  {
    return System.nanoTime();
  }

  public static long getResolution()
  {
    return 1000L;
  }
}