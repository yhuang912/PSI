// Copyright (C) 2011 by Yan Huang <yhuang@virginia.edu>

package YaoGC.Modules;

import YaoGC.State;

public interface Module {
    State[] execute(State[] s);
    State execute(State s);
}