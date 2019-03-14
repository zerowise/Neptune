package com.github.zerowise.neptune;

import java.util.HashMap;

import com.github.zerowise.neptune.invoke.RpcInvoker;
import com.github.zerowise.neptune.provider.NeptuneProvider;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
      
    	RpcInvoker rpcInvoker = new RpcInvoker(new HashMap<>());
    	NeptuneProvider provider = new NeptuneProvider();
    	provider.start(rpcInvoker, 8899);
    }
}
