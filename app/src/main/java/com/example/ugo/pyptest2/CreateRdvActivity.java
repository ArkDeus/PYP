package com.example.ugo.pyptest2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;

/**
 * Classe pour créer un Rendez-Vous sur l'application, et l'envoyer sur la Database (Firebase)
 */

public class CreateRdvActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMapClickListener {

    //Objet Google Maps pour afficher la map à l'écran et interagir avec
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    //La position de l'utilisateur actuellement
    private Location mLastLocation;

    //Marqueur pour savoir si un lieu de rendez-vous a déjà été sélectionné
    boolean firstClick = true;
    //Le Marqueur de rendez-vous
    Marker rdvMarker;

    //Elements d'interface (date, nom du rendez vous, heure)
    private TextView date;
    private TextView name;
    private TextView time;
    //Bouton de confirmation
    private Button confirm;

    //Le point de rendez vous en LatLng
    LatLng meetingPoint;

    //Les entités Firebase (database)
    FirebaseDatabase database;
    DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_rdv);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        date = (TextView) findViewById(R.id.date);
        time = (TextView) findViewById(R.id.time);
        name = (TextView) findViewById(R.id.name);
        confirm = (Button) findViewById(R.id.confirm);

        //Location API activation
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(AppIndex.API).build();
        }

        // Connect to the Firebase database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Get a reference to the centroids child items it the database
        DatabaseReference myRef = database.getReference("centroids");

/**
 * Ecouteur pour le bouton confirm, lorsuq'on appuie sur Confirm :
 * On sauvegarde le nouveau rendez-vous dans la base de données,
 * on sauvegarde le point de rendez-vous dans la base de données (deux entrées séparées).
 * L'entrée RDVs est l'ensemble des rendez-vous créés par les utilisateurs dans la base de données,
 * l'entrée dataPoints est l'ensemble des points (coordonnées latitude longitude) utilisés pour les RDVs
 * On peut supprimer un RDV de la base de données, mais on doit garder l'historique de tous les
 * points utilisés si on veut tirer parti de l'apprentissage machine, d'où les deux entrées différentes.
 */
        confirm.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (meetingPoint != null) {
                    DatabaseReference mref = FirebaseDatabase.getInstance().getReference("RDVs");
                    String sdate = date.getText().toString();
                    String stime = time.getText().toString();
                    String sname = name.getText().toString();
                    //Utilisateur connecté
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String creator = user.getEmail();
                    RDV rdv = new RDV(sname, sdate, stime, meetingPoint.latitude, meetingPoint.longitude, creator);
                    String rdvId = mref.push().getKey();
                    mref.child(rdvId).setValue(rdv);
                    //Envoie de la valeur à la base de données
                    mref = FirebaseDatabase.getInstance().getReference("dataPoints");
                    Centroid point = new Centroid(meetingPoint.latitude,meetingPoint.longitude);
                    String pointId = mref.push().getKey();
                    mref.child(pointId).setValue(point);
                    //Retour au menu principal
                    Intent intent = new Intent(CreateRdvActivity.this, MainActivity.class);
                    startActivity(intent);

                }
            }
        });
    }

    //Appel lorsqu'on est connecté aux services Google Play etc. (Activité prête à être utilisée)
    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        //Récupère la position de l'utilisateur
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            //Affiche la position de l'utilisateur sur la carte
            LatLng position = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(position).title("Your position").snippet("Your position")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            //Centre la carte sur la position de l'utilisateur
            mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
        }
        //Connexion avec la database pour récupérer les "centroids", c'est à dire les points agrégés ou "clusters"
        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("centroids");
        //Code pour remplir la map avec les centroids calculés par l'algorithme de traitement des données
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //mMap.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    //Création d'un centroid à partir de la database
                    Centroid centroid = snap.getValue(Centroid.class);
                    //On récupère le centroid, et on affiche une zone de 50 mètres autour de celui ci
                    LatLng centroidLatLng = new LatLng(centroid.getLatitude(), centroid.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(centroidLatLng).title("Hotspot").snippet("Hotspot"));
                    Circle circle = mMap.addCircle(new CircleOptions()
                            .center(centroidLatLng)
                            .radius(50)
                            .strokeColor(0x757B0000)
                            .fillColor(0x357B0000));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // We are not connected anymore!
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // We tried to connect but failed!
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Création d'un listener sur la map
        mMap.setOnMapClickListener(this);


    }

    //Lorsque l'on clique sur la map, on créé un nouveau marker qui correspond au rdv et on focus la map dessus
    @Override
    public void onMapClick(LatLng point) {
        if (!firstClick) {
            rdvMarker.remove();
        } else {
            firstClick = false;
        }
        rdvMarker = mMap.addMarker(new MarkerOptions().position(point).title("Your meeting point").snippet("Meeting point")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        meetingPoint = point;
    }
}
