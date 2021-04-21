package com.kemriwellcome.dm.prisms.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.fxn.stash.Stash;
import com.kemriwellcome.dm.prisms.R;
import com.kemriwellcome.dm.prisms.dependencies.Constants;
import com.kemriwellcome.dm.prisms.models.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class SmsFragment extends Fragment {


    private Unbinder unbinder;
    private View root;
    private Context context;

    private User loggedInUser;


    @BindView(R.id.shimmer_my_container)
    ShimmerFrameLayout shimmer_my_container;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.no_studies)
    LinearLayout no_studies;


    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        this.context = ctx;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_sms, container, false);
        unbinder = ButterKnife.bind(this, root);

        loggedInUser = (User) Stash.getObject(Constants.USER, User.class);


        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);

        //set data and list adapter
        //recyclerView.setAdapter(mAdapter);


//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                if (!recyclerView.canScrollHorizontally(1)) {
//                    if (myShouldLoadMore && !MY_NEXT_LINK.equals("null")) {
//                        loadMore();
//                    }
//                }
//            }
//        });

//        mAdapter.setOnItemClickListener(new ResourcesAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                Resource resource = resourceArrayList.get(position);
//
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("resource", resource);
//                NavHostFragment.findNavController(CMESTabFragment.this).navigate(R.id.nac_resource_details, bundle);
//            }
//        });




        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        shimmer_my_container.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        shimmer_my_container.stopShimmerAnimation();
        super.onPause();
    }


//    private void firstLoad() {
//
//
//        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
//                Stash.getString(Constants.END_POINT)+ Constants.WALLET_TRANSACTIONS, null, new Response.Listener<JSONObject>() {
//
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//
////                    Log.e("resoponse", response.toString());
//
//                    walletTransactionArrayList.clear();
//
//                    myShouldLoadMore = true;
//                    recyclerView.setVisibility(View.VISIBLE);
//
//                    if (shimmer_my_container!=null){
//                        shimmer_my_container.stopShimmerAnimation();
//                        shimmer_my_container.setVisibility(View.GONE);
//                    }
//
//
//                    boolean  status = response.has("success") && response.getBoolean("success");
//                    String message = response.has("message") ? response.getString("message") : "" ;
//                    String errors = response.has("errors") ? response.getString("errors") : "" ;
//
//
//                    if (status){
//                        JSONArray myArray = response.getJSONArray("data");
//                        JSONObject links = response.getJSONObject("links");
//                        MY_NEXT_LINK = links.getString("next");
//
//                        if (myArray.length() > 0){
//
//                            no_transactions.setVisibility(View.GONE);
//
//
//                            for (int i = 0; i < myArray.length(); i++) {
//
//                                JSONObject item = (JSONObject) myArray.get(i);
//
//
//                                int  id = item.has("id") ? item.getInt("id") : 0;
//                                int  wallet_id = item.has("wallet_id") ? item.getInt("wallet_id") : 0;
//                                String amount = item.has("amount") ? item.getString("amount") : "";
//                                String transaction_type = item.has("transaction_type") ? item.getString("transaction_type") : "";
//                                String source = item.has("source") ? item.getString("source") : "";
//                                String trx_id = item.has("trx_id") ? item.getString("trx_id") : "";
//                                String narration = item.has("narration") ? item.getString("narration") : "";
//                                String created_at = item.has("created_at") ? item.getString("created_at") : "";
//
//                                WalletTransaction walletTransaction = new WalletTransaction(id,wallet_id,amount,transaction_type,source,trx_id,narration,created_at);
//
//                                walletTransactionArrayList.add(walletTransaction);
//                                mAdapter.notifyDataSetChanged();
//
//                            }
//
//                        }else {
//                            //not data found
//                            no_transactions.setVisibility(View.VISIBLE);
//                        }
//                    }else {
//                        Dialogs.showWarningDialog(context,message,errors);
//
//                    }
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                myShouldLoadMore =true;
//
//                VolleyLog.d("VOLLEY ERROE", "Error: " + error.getMessage());
//                MainActivity.getInstance().snack(VolleyErrors.getVolleyErrorMessages(error, context));
//
//            }
//        }){
//            /*
//             * Passing some request headers
//             */
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Authorization", loggedInUser.getToken_type()+" "+loggedInUser.getAccess_token());
//                headers.put("Content-Type", "application/json");
//                headers.put("Accept", "application/json");
//                return headers;
//            }
//        };
//
//        AfyacashApplication.getInstance().addToRequestQueue(jsonObjReq);
//    }



}