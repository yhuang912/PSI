// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package Program;

import java.io.*;

import YaoGC.*;
import YaoGC.Modules.*;

public abstract class ProgCommon {
    public static ObjectOutputStream oos        = null;              // socket output stream
    public static ObjectInputStream  ois        = null;              // socket input stream
    // public static Circuit[] ccs;
    public static Module mod;
}