package se.psilon.migomipo.migol2;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public class FlyweightStore<A> {
    private WeakHashMap<A,WeakReference<A>> map =
            new WeakHashMap<A, WeakReference<A>>();
        
    public A get(A obj){
        
        WeakReference<A> v2 = map.get(obj);
        A ret = null;
        if(v2 != null){
            ret = v2.get();
        }
        if(ret == null){
            ret = obj;
            map.put(obj, new WeakReference<A>(obj));
        }

        return ret;
    }
    
}
