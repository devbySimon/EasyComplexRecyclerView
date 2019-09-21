package bysimon.developer.easycomplexrecyclerview;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListenerManager {
    
    
    
    public static final int KEY_APP_STORE_HIGHLIGHT = 1;
    public static final int KEY_APP_REMOVE_HIGHLIGHT = 2;
    public static final int KEY_APP_STORE_STANDARD = 3;
    public static final int KEY_APP_REMOVE_STANDARD = 4;
    public static final int KEY_APP_STORE_PRIORITY = 5;
    public static final int KEY_APP_REMOVE_PRIORITY = 6;
    public static final int KEY_APP_STORE_GAME = 7;
    public static final int KEY_APP_REMOVE_GAME = 8;
    public static final int KEY_NOTIFICATION_NEW = 9;
    public static final int KEY_NOTIFICATION_DELETE = 10;
    public static final int KEY_DATALOGGER = 11;
    public static final int KEY_CALLPHPACTION = 12;
    public static final int KEY_NOTIFICATION_STORE = 13;
    public static final int KEY_NOTIFICATION_REMOVE = 14;
    
    private HashMap< Integer , List< IListenerHandler > > listenerGroups = new HashMap<>(  );
    private ArrayList< RecyclerAdapter > registeredAdapters = new ArrayList<>(  );
    
    
    
    private Activity activity;
    public ListenerManager( Activity activity ) {
        this.activity = activity;
    }
    public Activity getActivity() {
        return activity;
    }
    
    public void registerAdapter( RecyclerAdapter adapter ) {
        this.registeredAdapters.add( adapter );
    }
    public void unregisterAdapter( RecyclerAdapter adapter ) {
        this.registeredAdapters.remove( adapter );
    }
    
    public void updateItem( IRecycler_Item item ) {
        for( RecyclerAdapter adapters : registeredAdapters )
            adapters.updateSingleItem( item );
    }
    public void updateAllAdapters() {
        for( RecyclerAdapter adapter : registeredAdapters )
            adapter.updateAllItems();
    }
    
    public void runOnAsyncTask( final ISimpleAction action ) {
        getActivity().runOnUiThread( new Runnable() {
            @Override
            public void run () {
                AsyncTask.execute( new Runnable() {
                    @Override
                    public void run () {
                        action.execute();
                    }
                } );
            }
        } );
    }
    
    
    
    public void notify( Context context , int KEY , Object object ) {
        
        if(listenerGroups.containsKey( KEY )) {
            for( IListenerHandler handler : listenerGroups.get( KEY ))
                handler.superNotifiy( context , object , KEY , this );
        }
    }
    
    public void add( IListenerHandler listenerHandler , int KEY ) {
        
        if(listenerGroups.containsKey( KEY ))
            listenerGroups.get( KEY ).add( listenerHandler );
        else {
            ArrayList< IListenerHandler > listenerHandlers = new ArrayList<>(  );
            listenerHandlers.add( listenerHandler );
            listenerGroups.put( KEY, listenerHandlers );
        }
    }
    
    public void remove( IListenerHandler listenerHandler , int KEY ) {
        
        /*if(appListerners.containsKey( KEY ))
            appListerners.get( KEY ).add( item );
        else {
            ArrayList<IRecycler_Item> items = new ArrayList<>(  );
            items.add( item );
            appListerners.put( KEY, items );
        }*/
    }
    
    private void cleanUp() {
        
        ArrayList<RecyclerAdapter> adaptersToRemove = new ArrayList<>(  );
        
        for(RecyclerAdapter adapter : registeredAdapters) {
            
            try {
                adapter.updateAllItems();
            }
            catch ( Exception e ) {
                System.out.println( "Unregister Adapter (ERROR)" );
                adaptersToRemove.add( adapter );
            }
        }
        
        for(RecyclerAdapter adapter : adaptersToRemove)
            unregisterAdapter( adapter );
    }
}
