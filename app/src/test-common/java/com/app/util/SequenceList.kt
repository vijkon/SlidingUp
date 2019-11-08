package com.app.mercari.util

import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

class SequenceList<T> constructor(results : List<T>) : Answer<T>
{
    private var resultIterator : Iterator<T> = results.iterator()
    // the last element is always returned once the iterator is exhausted, as with thenReturn()
    private val last: T = results.get(results.size - 1)

    override fun answer(invocation: InvocationOnMock?): T {
        if (resultIterator.hasNext())
        {
            return resultIterator.next();
        }
        return last
    }

}