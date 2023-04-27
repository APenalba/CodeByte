package edu.pis.codebyte.view.home;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import edu.pis.codebyte.R;
import edu.pis.codebyte.model.RecyclerViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private ArrayList<RecyclerViewAdapter.MyData> mData;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment home.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflar la vista del fragmento
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // encontrar la vista RecyclerView en la vista inflada
        recyclerView = rootView.findViewById(R.id.recycler_view_lenguajes);

        // configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // crear los datos
        mData = new ArrayList<>();
        mData.add(new RecyclerViewAdapter.MyData(R.drawable.logo_c));
        mData.add(new RecyclerViewAdapter.MyData(R.drawable.logo_cpp));
        mData.add(new RecyclerViewAdapter.MyData(R.drawable.logo_html));

        // crear el adaptador y establecerlo en RecyclerView
        adapter = new RecyclerViewAdapter(mData);
        recyclerView.setAdapter(adapter);

        return rootView;
    }
}