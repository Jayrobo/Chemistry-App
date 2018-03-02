package com.androidplot.util;

import java.util.List;
import java.util.Set;

public abstract class MultiSynch {

    public interface Action {
        void run(Object[] objArr);
    }

    public static void run(Object[] params, Set synchSet, Action action) {
        run(params, synchSet.toArray(), action, 0);
    }

    public static void run(Object[] params, List synchList, Action action) {
        run(params, synchList.toArray(), action, 0);
    }

    public static void run(Object[] params, Object[] synchArr, Action action) {
        run(params, synchArr, action, 0);
    }

    private static void run(Object[] params, Object[] synchArr, Action action, int depth) {
        if (synchArr != null) {
            synchronized (synchArr[depth]) {
                if (depth < synchArr.length - 1) {
                    run(params, synchArr, action, depth + 1);
                } else {
                    action.run(params);
                }
            }
        }
        action.run(params);
    }
}
