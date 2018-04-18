package lokavidya.iitb.com.lvcreate.volley;


import lokavidya.iitb.com.lvcreate.network.NetworkException;

public interface RequestResponseListener {

    interface Listener{
        <T> void onResponse(T response);
    }

    interface ErrorListener{
        void onError(NetworkException error);
    }

}
