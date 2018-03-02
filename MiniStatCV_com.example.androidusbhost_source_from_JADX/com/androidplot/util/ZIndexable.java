package com.androidplot.util;

import java.util.List;

public interface ZIndexable<ElementType> {
    List<ElementType> elements();

    boolean moveAbove(ElementType elementType, ElementType elementType2);

    boolean moveBeneath(ElementType elementType, ElementType elementType2);

    boolean moveDown(ElementType elementType);

    boolean moveToBottom(ElementType elementType);

    boolean moveToTop(ElementType elementType);

    boolean moveUp(ElementType elementType);
}
