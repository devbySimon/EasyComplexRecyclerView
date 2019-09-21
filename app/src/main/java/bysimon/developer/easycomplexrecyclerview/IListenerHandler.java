package bysimon.developer.easycomplexrecyclerview;

import android.content.Context;

public abstract class IListenerHandler {
    
    private IListenerHandler listenerHandlerOverride = null;
    private Class objectType = null;
    
    public IListenerHandler () {
    
    }
    
    public IListenerHandler ( Class objectType) {
        
        this.objectType = objectType;
    }
    
    /**
    Get's called by @Listenermanager for registered KEY's
    
    if objectType is set, execution is aborted if given object.Class does not match expected object.Class
     */
    public final void superNotifiy( Context context , Object object , int KEY , ListenerManager listenerManager  ) {
        
        if(objectType != null && object.getClass() != objectType) {
                return;
        }
        
        if(listenerHandlerOverride != null) {
            if(listenerHandlerOverride.notify( context, object, KEY, listenerManager ))
                this.notify( context, object, KEY, listenerManager );
        }
        else
            this.notify( context, object, KEY, listenerManager );
    }
    
    /**
    OVERRIDE
    
    return TRUE if overridden shall be also executed, if not return FALSE
     */
    public boolean notify( Context context , Object object , int KEY , ListenerManager listenerManager  ) {
        
        return false;
    }
    
    /**
    Add IListenerHandler on super if notify() shall be overriden, but not permanently
    
    unregisterOverride() on super.destory()
     */
    public final void registerOverride( IListenerHandler listenerHandlerOverride ) {
        
        this.listenerHandlerOverride = listenerHandlerOverride;
    }
    
    /**
     Call this on super.destroy()
     Clears the overrideListenerHandler
     */
    public final void unregisterOverride() {
        
        this.listenerHandlerOverride = null;
    }
}
