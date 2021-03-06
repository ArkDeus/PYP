package com.example.ugo.pyptest2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * Fragment pour la visualisation de la liste des Rendez-Vous
 * Ce fragment permet de voir tous les rendez-vous créés par les utilisateurs
 * Ce fragment est affiché dans la Main Activity
 */

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListRDVFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListRDVFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListRDVFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //La liste des rendez vous récupérés sur firebase
    private ArrayList<RDV> listRDV = new ArrayList<RDV>();

    private FloatingActionButton fab;

    private OnFragmentInteractionListener mListener;

    public ListRDVFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListRDVFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListRDVFragment newInstance(String param1, String param2) {
        ListRDVFragment fragment = new ListRDVFragment();
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

    //On attache le onclicklistener sur le bouton qui permet de créer un nouveau rendez vous
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list_rdv, container, false);
        fab = (FloatingActionButton) view.findViewById(R.id.fab2);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Lance l'activité de création de rendez vous quand on clic sur le bouton
                Intent intent = new Intent(getActivity(), CreateRdvActivity.class);
                startActivity(intent);
                //finish();

            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //C'est lors de onActivityCreated que l'on va remplir la ListView qui contient tous les RDVs créés par les utilisateurs
    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        //La listview en question qui contiendra tous les RDVs
        ListView messagesView = (ListView) getView().findViewById(R.id.list_rdv);
        //La database qui stocke les RDVs
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("RDVs");

        /**
         * On créé un FireBaseListAdapter, qui est un adapter pour les listview et qui permet de créer des items
         en se connectant directement à FireBase. La Liste est mise à jour dynamiquement
         lorsque de nouveaux rendez-vous sont créés.
         Cet adapter prend en paramètre l'activité en cours, la classe Java à mapper avec les objets de la database (RDV),
         un layout pour les items de la listview et une référence à la database (donc les RDVs dans la database)
         */

        FirebaseListAdapter<RDV> mAdapter = new FirebaseListAdapter<RDV>(getActivity(), RDV.class, android.R.layout.two_line_list_item, ref) {
            //populateView permet de compléter la listview en utilisant les RDVs de la base de données
            @Override
            protected void populateView(View view, RDV rdv, int position) {
                ((TextView)view.findViewById(android.R.id.text1)).setText(rdv.getName()+" at "+rdv.getDate()+", "+rdv.getTime());
                ((TextView)view.findViewById(android.R.id.text2)).setText("created by "+rdv.getCreator());
                //Mise en mémoire du rendez vous à la position en question
                listRDV.add(position,rdv);
            }
        };
        //On place l'adapter dans la listview
        messagesView.setAdapter(mAdapter);
        //On attache les listeners sur les items de la liste
        messagesView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>adapter,View v, int position, long id){
                //On récupère le rendez vous qui correspond à l'item de la liste selectionné
                RDV rdv = listRDV.get(position);
                Log.i("test",rdv.getName());
                //On ouvre le service directions associé à l'adresse du rdv pour calculer l'itinéraire
                String uri = "http://maps.google.com/maps?f=d&hl=en&daddr="+rdv.getLatitude()+","+rdv.getLongitude();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(Intent.createChooser(intent, "Select an application"));
            }
        });

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
