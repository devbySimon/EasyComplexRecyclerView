package bysimon.developer.easycomplexrecyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static bysimon.developer.easycomplexrecyclerview.IRecycler_Item.RENDERTYPE_EXPANDED;
import static bysimon.developer.easycomplexrecyclerview.IRecycler_Item.RENDERTYPE_HIDDEN;
import static bysimon.developer.easycomplexrecyclerview.IRecycler_Item.RENDERTYPE_NORMAL;
import static bysimon.developer.easycomplexrecyclerview.IRecycler_Item.RENDERTYPE_SMALL;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    
    private ArrayList< IRecycler_Item > items = new ArrayList<>(  );
    
    private RecyclerView rootRecyclerView;
    private Context context;
    private View view1;
    private RecyclerView.ViewHolder viewHolder1;
    private ListenerManager listenerManager;
    
    public RecyclerView.RecycledViewPool viewPool;
    
    public RecyclerAdapter( RecyclerView rootRecyclerView , Context context , ListenerManager listenerManager ) {
        
        this.rootRecyclerView = rootRecyclerView;
        this.context = context;
        this.listenerManager = listenerManager;
        listenerManager.registerAdapter( this );
        
        viewPool = new RecyclerView.RecycledViewPool();
    }
    
    public RecyclerAdapter( Context context , ListenerManager listenerManager ) {
        
        this.context = context;
        this.listenerManager = listenerManager;
        listenerManager.registerAdapter( this );
        
        viewPool = new RecyclerView.RecycledViewPool();
    }
    
    public RecyclerAdapter( Context context , ListenerManager listenerManager, boolean registerAdapter ) {
        
        this.context = context;
        this.listenerManager = listenerManager;
        
        if(registerAdapter)
            listenerManager.registerAdapter( this );
        
        viewPool = new RecyclerView.RecycledViewPool();
    }
    
    public void add( IRecycler_Item item ) {
        
        int pos = 0;
        while(pos < items.size() && items.get( pos ).getPriority() >= item.getPriority())
            pos++;
        
        items.add( pos, item );
    }
    
    public void remove( IRecycler_Item item ) {
    
        items.remove( item );
    }
    
    public void remove(  ) {
        
        items = new ArrayList<>(  );
    }
    
    public void swapItems( IRecycler_Item item1, IRecycler_Item item2 ) {
    
        if(items.contains( item1 ) && items.contains( item2 )) {
    
            int pos1 = items.indexOf( item1 );
            int pos2 = items.indexOf( item2 );
            
            items.set( pos1, item2 );
            items.set( pos2, item1 );
            
            updateSingleItem( item1 );
            updateSingleItem( item2 );
        }
    }
    
    public void updateSingleItem( IRecycler_Item item ) {
        
        this.notifyItemChanged( items.indexOf( item ) );
    }
    
    public void updateSingleItem( int itemPosition ) {
        
        this.notifyItemChanged( itemPosition );
    }
    
    public void updateAllItems() {
        
        this.notifyDataSetChanged();
    }
    
    public void onDestroy( Context context ) {
        
        for( IRecycler_Item item : items)
            item.onDestroy( context , this );
    }
    
    public ListenerManager getListenerManager() {
        
        return listenerManager;
    }
    
    public void scrollTo( int itemPosition ) {
        
        if(rootRecyclerView != null) {
            this.rootRecyclerView.stopScroll();
            this.rootRecyclerView.smoothScrollToPosition( itemPosition );
        }
    }
    
    public void scrollTo( IRecycler_Item item ) {
        
        if(rootRecyclerView != null) {
            this.rootRecyclerView.stopScroll();
            this.rootRecyclerView.smoothScrollToPosition( items.indexOf( item ) );
        }
    }
    
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType) {
        
        if(viewType == 0)
            view1 = LayoutInflater.from( context ).inflate( R.layout.view_null , parent, false);
        else
            view1 = LayoutInflater.from( context ).inflate( viewType , parent, false);
        
        viewHolder1 = new RecyclerView.ViewHolder(view1) {
            @Override
            public String toString() {
                return super.toString();
            }
        };
        
        /*if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            viewHolder1.getLayoutTransition().enableTransitionType( LayoutTransition.CHANGING );
        }*/
        
        return viewHolder1;
    }
    
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        
        items.get( position ).executeQueueTransform( holder.itemView, this );
        
        if(items.get( position ).getRenderType() != RENDERTYPE_HIDDEN) {
    
            switch ( items.get( position ).getRenderType() ) {
                case RENDERTYPE_SMALL: {
    
                    items.get( position ).renderSmall( holder.itemView, context, this );
                    break;
                }
                case RENDERTYPE_NORMAL: {
    
                    items.get( position ).renderNormal( holder.itemView, context, this );
                    break;
                }
                case RENDERTYPE_EXPANDED: {
    
                    items.get( position ).renderExpanded( holder.itemView, context, this );
                    break;
                }
            }
        }
    }
    
    @Override
    public int getItemCount(){
        
        return items.size();
    }
    
    @Override
    public int getItemViewType ( int position ) {
        
        return items.get( position ).getType();
    }
}
