package bysimon.developer.easycomplexrecyclerview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

public class IRecycler_Item {
    
    public static final int PRIORITY_NONE = 0;
    public static final int PRIORITY_LOW = 1;
    public static final int PRIORITY_DEFAULT = 2;
    public static final int PRIORITY_HIGH = 3;
    public static final int PRIORITY_HIGHEST = 4;
    
    public static final int RENDERTYPE_HIDDEN = 0;
    public static final int RENDERTYPE_SMALL = 1;
    public static final int RENDERTYPE_NORMAL = 2;
    public static final int RENDERTYPE_EXPANDED = 3;
    
    protected long animDuration = 200; //can be changed in @IRecycler_Item or in Item
    
    protected int layoutId_SMALL;
    protected int layoutId_NORMAL; //DEFAULT (needs to be set in Constructor)
    protected int layoutId_EXPANDED;
    
    private int currentRenderType = RENDERTYPE_NORMAL; //set via: transformToRenderType() / setRenderType()
    
    private boolean queueRenderTypeTransform = false; //ignore
    private int queueRenderType; //ignore
    
    private HashMap< Integer , IListenerHandler > listenerHandlerHashMap;
    
    /**
    OVERRIDE
    Render your Layout Small
     */
    public void renderSmall( View view , Context context , RecyclerAdapter adapter ) {
    
    }
    
    /**
    OVERRIDE
    Render your Layout Normal (DEFAULT)
     */
    public void renderNormal( View view , Context context , RecyclerAdapter adapter ) {
    
    }
    
    /**
    OVERRIDE
    Render your Layout Expanded
     */
    public void renderExpanded( View view , Context context , RecyclerAdapter adapter ) {
    
    }
    
    /**
    Set's up one Button's that can be clicked or holded
    If other actions wanted, change this function or OVERRIDE it for specific Items
     */
    public void linkButton( View view , final Context context , final RecyclerAdapter adapter , final int buttonId ) {
        
        final int renderType = this.currentRenderType;
        
        final View button = view.findViewById( buttonId );
        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick ( View v ) {
    
                if(renderType != currentRenderType)
                    return;
                
                switch ( currentRenderType ) {
                    case RENDERTYPE_SMALL: {
                        onButtonClickSmall( buttonId, button, context, adapter );
                        break;
                    }
                    case RENDERTYPE_NORMAL: {
                        onButtonClickNormal( buttonId, button, context, adapter );
                        break;
                    }
                    case RENDERTYPE_EXPANDED: {
                        onButtonClickExpanded( buttonId, button, context, adapter );
                        break;
                    }
                }
            }
        } );
        button.setOnLongClickListener( new View.OnLongClickListener() {
            @Override
            public boolean onLongClick ( View v ) {
    
                if(renderType != currentRenderType)
                    return false;
                
                switch ( currentRenderType ) {
                    case RENDERTYPE_SMALL: {
                        onButtonHoldSmall( buttonId, button, context, adapter );
                        break;
                    }
                    case RENDERTYPE_NORMAL: {
                        onButtonHoldNormal( buttonId, button, context, adapter );
                        break;
                    }
                    case RENDERTYPE_EXPANDED: {
                        onButtonHoldExpanded( buttonId, button, context, adapter );
                        break;
                    }
                }
                return true;
            }
        } );
    }
    
    /**
    Set's up multiple Button's that can be clicked or holded
    if more actions wanted, change linkButton( View view , final Context context , final RecyclerAdapter adapter , final int buttonId )
     */
    public void linkButton( View view , Context context , RecyclerAdapter adapter , int[] buttonId ) {
        
        for(int id : buttonId) {
            linkButton( view, context, adapter, id);
        }
    }
    
    /**
    OVERRIDE
    Get's called when linked View in renderSmall is being Clicked
     */
    public void onButtonClickSmall( int buttonId , View button , final Context context , final RecyclerAdapter adapter ) {
    
    }
    
    /**
    OVERRIDE
    Get's called when linked View in renderSmall is being holded
     */
    public void onButtonHoldSmall( int buttonId , View button, final Context context , final RecyclerAdapter adapter ) {
    
    }
    
    /**
    OVERRIDE
    Get's called when linked View in renderNormal is being Clicked
     */
    public void onButtonClickNormal( int buttonId , View button, final Context context , final RecyclerAdapter adapter ) {
    
    }
    
    /**
    OVERRIDE
    Get's called when linked View in renderNormal is being holded
     */
    public void onButtonHoldNormal( int buttonId , View button, final Context context , final RecyclerAdapter adapter ) {
    
    }
    
    /**
    OVERRIDE
    Get's called when linked View in renderExpanded is being Clicked
     */
    public void onButtonClickExpanded( int buttonId , View button, final Context context , final RecyclerAdapter adapter ) {
    
    }
    
    /**
    OVERRIDE
    Get's called when linked View in renderExpanded is being holded
     */
    public void onButtonHoldExpanded( int buttonId , View button, final Context context , final RecyclerAdapter adapter ) {
    
    }
    
    /**
    Returns the current RENDER_TYPE to @RecyclerAdapter
     */
    public int getRenderType() {
        
        return this.currentRenderType;
    }
    
    /**
    Change RENDER_TYPE with animation
    animation can be queued with rootView = null or using transformToRenderType( int newRenderType )
     */
    public void transformToRenderType( final View rootView, final RecyclerAdapter adapter, final int newRenderType ) {
        
        if(rootView == null) {
            transformToRenderType( newRenderType );
            return;
        }
        
        ValueAnimator anim = ValueAnimator.ofInt( rootView.getMeasuredHeight(), newRenderType > currentRenderType ? 100 : -100);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = rootView.getLayoutParams();
                layoutParams.height = val;
                rootView.setLayoutParams(layoutParams);
            }
        });
        anim.addListener( new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd ( Animator animation ) {
                currentRenderType = newRenderType;
                adapter.updateSingleItem( IRecycler_Item.this );
            }
        } );
        anim.setDuration(animDuration);
        anim.start();
    }
    
    /**
    Queue animated RENDER_TYPE change
     */
    public void transformToRenderType( int newRenderType ) {
    
        this.queueRenderType = newRenderType;
        this.queueRenderTypeTransform = true;
    }
    
    /**
    Get's called by @RecyclerAdapter, executed after render
     */
    public void executeQueueTransform( final View rootView, final RecyclerAdapter adapter ) {
        
        if(this.queueRenderTypeTransform) {
            transformToRenderType( rootView, adapter, this.queueRenderType );
            this.queueRenderTypeTransform = false;
        }
    }
    
    /**
    Change RENDER_TYPE without animation
     */
    public void setRenderType( int newRenderType ) {
    
        queueRenderTypeTransform = false;
        this.currentRenderType = newRenderType;
    }
    
    /**
    OVERRIDE
    Get's called on removal of IRecycler_Item or Activity onDestroy (if added)
    call super.onDestroy() if listenerHandlers registered
     */
    public void onDestroy( Context context, RecyclerAdapter adapter ) {
        
        if(listenerHandlerHashMap != null) {
            for( Map.Entry< Integer , IListenerHandler > entry : listenerHandlerHashMap.entrySet()) {
                adapter.getListenerManager().remove( entry.getValue(), entry.getKey() );
            }
        }
    }
    
    /**
    Returns the current Layout Id to @RecyclerAdapter
     */
    public int getType() {
        
        if(currentRenderType == RENDERTYPE_SMALL)
            return layoutId_SMALL;
        else if(currentRenderType == RENDERTYPE_NORMAL)
            return layoutId_NORMAL;
        else if(currentRenderType == RENDERTYPE_EXPANDED)
            return layoutId_EXPANDED;
        else
            return R.layout.view_null;
    }
    
    /**
    OVERRIDE
     */
    public String getIdentifier() {
        
        return "";
    }
    
    /**
    OVERRIDE
     */
    public String getSecondaryIdentifier() {
        
        return "";
    }
    
    /**
    OVERRIDE
     */
    public String getIdentifier(boolean secondary) {
        
        if(secondary)
            return getSecondaryIdentifier();
        else
            return getIdentifier();
    }
    
    /**
    Insert new IListenerHandler that can be Overridden
    If IListenerHandler shall not be overridden, call listenerManager.add( new IListenerHandler , KEY ) yourself
     */
    public final void registerListernerHanlder( ListenerManager listenerManager , IListenerHandler listenerHandler , int KEY ) {
    
        listenerManager.add( listenerHandler, KEY );
        
        if(listenerHandlerHashMap == null)
            listenerHandlerHashMap = new HashMap<>(  );
        
        listenerHandlerHashMap.put( KEY, listenerHandler );
    }
    
    /**
    Returns the IListenerHandler for the given KEY
    Use to register/unregister ListenerHandlerOverride
     */
    public final IListenerHandler getListenerHandler( int KEY ) {
        
        if(this.listenerHandlerHashMap != null) {
            if(this.listenerHandlerHashMap.containsKey( KEY ))
                return this.listenerHandlerHashMap.get( KEY );
            else
                return null;
        } else
            return null;
    }
    
    /**
    OVERRIDE
    Returns the Item-Priority to @RecyclerAdapter
    Item's get sorted by Priority
     */
    public int getPriority() {
        
        return PRIORITY_DEFAULT;
    }
    
    /**
    OVERRIDE
    Can be called to e.g. reset all Item values
     */
    public void flushContent( Context context ) {
    
    
    }
    
    /**
    OVERRIDE
    Can be called to e.g. load new Item values
     */
    public void flushContent( Context context, Object object ) {
    
    
    }
    
    /**
    OVERRIDE
     */
    public Object getContent(Object object) {
    
        return null;
    }
}
