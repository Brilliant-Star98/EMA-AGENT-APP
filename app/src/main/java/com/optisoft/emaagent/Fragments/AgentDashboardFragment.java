package com.optisoft.emaagent.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.optisoft.emaagent.HelperClasses.CommonPrefrence;
import com.optisoft.emaagent.Model.ResponseModel;
import com.optisoft.emaagent.Model.UserModel;
import com.optisoft.emaagent.R;
import com.optisoft.emaagent.Webservices.CallApi;
import com.google.gson.Gson;

import org.json.JSONObject;

import static com.optisoft.emaagent.HelperClasses.ApiConstant.EXCEPTION_TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgentDashboardFragment extends Fragment {

    private TextView tv_total, tv_payed, tv_unpayed, tv_distress, tv_contact;
    private CommonPrefrence commonPrefrence = new CommonPrefrence();
    private UserModel userModel = null;
    private CallApi callApi = new CallApi();
    private Gson gson = new Gson();

    public AgentDashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agent_dashboard, container, false);

        userModel = commonPrefrence.getUserLoginSharedPref(getActivity());

        tv_total    = (TextView) view.findViewById(R.id.tv_total);
        tv_payed    = (TextView) view.findViewById(R.id.tv_payed);
        tv_unpayed  = (TextView) view.findViewById(R.id.tv_unpayed);
        tv_distress = (TextView) view.findViewById(R.id.tv_distress);
        tv_contact  = (TextView) view.findViewById(R.id.tv_contact);

        callApi.requestAgentData(this, userModel.getUserId());
        return view;
    }

    public void responseAgentData(ResponseModel model) {
        try {
            if(model.getStatus()==1)
            {
                JSONObject jsonObject = new JSONObject(gson.toJson(model.getResponse()));
                tv_total.setText(jsonObject.getInt("total")+"");
                tv_payed.setText(jsonObject.getInt("payed")+"");
                tv_unpayed.setText(jsonObject.getInt("unpayed")+"");
                tv_distress.setText(jsonObject.getInt("distress")+"");
                tv_contact.setText(jsonObject.getInt("contact")+"");

            }else {
                customToast(model.getMessage());
            }
        }catch (Exception e){
            Log.e(EXCEPTION_TAG, e.getMessage());
            customToast(e.getMessage());
        }
    }

    public void customToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }
}
