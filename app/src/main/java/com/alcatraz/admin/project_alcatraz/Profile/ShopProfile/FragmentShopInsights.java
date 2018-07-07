package com.alcatraz.admin.project_alcatraz.Profile.ShopProfile;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RadialGradient;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.alcatraz.admin.project_alcatraz.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentShopInsights.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentShopInsights#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentShopInsights extends Fragment {


    private static String TAG="FragmentShopInsights";
    Button button;


    PieChart pieChart;
    LineChart lineChart;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //piechartdataadd
    private void addatapie(PieChart pieChart) {

        Log.d(TAG,"add data function");
        float ydata[]={30,70};
        String xdata[]={"Organic","Iorganic"};

//        ArrayList<PieEntry> yentry=new ArrayList<>();
//        ArrayList<String> xentry= new ArrayList<>();
        ArrayList<PieEntry> entries=new ArrayList<>();


        for(int i=0;i<xdata.length;i++)
        {
            entries.add(new PieEntry(ydata[i],xdata[i]));
        }

//        for(int i=0;i<ydata.length;i++)
//        {
//            yentry.add(new PieEntry(ydata[i],i));
//        }

        //create the data set
        PieDataSet dataSet=new PieDataSet(entries,"Type of Money");
        dataSet.setSliceSpace(3);
        dataSet.setValueTextSize(17);

        //adding colour to pie chart
        ArrayList<Integer> piecolors=new ArrayList<>();
        piecolors.add(Color.LTGRAY);
        piecolors.add(Color.CYAN);

        dataSet.setColors(piecolors);
        dataSet.setValueLinePart1OffsetPercentage(80f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        //adding legend to piechart
        Legend legend=pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        //create pie data object
        PieData piedata=new PieData(dataSet);
        pieChart.setData(piedata);
        pieChart.invalidate();



    }

    //linechartdata
    private void linedata(LineChart lineChart)
    {

    }


    public FragmentShopInsights() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentShopInsights.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentShopInsights newInstance(String param1, String param2) {
        FragmentShopInsights fragment = new FragmentShopInsights();
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Log.d(TAG,"onCreateView: starting to make a pie chart");
         View v= inflater.inflate(R.layout.fragment_shop_insights, container, false);
        //for setting up the pie chart descrition

       // settig up pie chart
        pieChart=(PieChart)v.findViewById(R.id.piechart);
        Description description = new Description();
        description.setTextColor(Color.BLACK);
        description.setText("Inorganic vs Organic Money");
        description.setTextSize(12);

        pieChart.setDescription(description);
        // making the transparent circle
        pieChart.setTransparentCircleAlpha(90);
        pieChart.setTransparentCircleRadius(65);



        pieChart.setCenterText("Organic vs Inorganic money");
        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(45);

        pieChart.setCenterText("graph");
        pieChart.setCenterTextSize(10);
        pieChart.setDrawEntryLabels(true);


        pieChart.animateXY(3000,3000);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d(TAG,"onValueSelected: Value select from chart");
                Log.d(TAG,"onValueSelected: "+e.toString());
                Log.d(TAG,"onValueSelected: "+h.toString());

            }

            @Override
            public void onNothingSelected() {

            }
        });

        //setting up line chart
        lineChart=(LineChart)v.findViewById(R.id.linechart);
        ArrayList<Integer> xdata=new ArrayList<>();
        ArrayList<Entry> yinorganic=new ArrayList<>();
        ArrayList<Entry> yorganic=new ArrayList<>();
        ArrayList<Entry> ycustomer=new ArrayList<>();
        //trying to show the data of customers and money of last 30 days
        Integer yo=1;
        int datapoints=30;
        Random random=new Random();
        for(int i=0;i<datapoints;i++)
        {
            yo= random.nextInt(300);
            yinorganic.add(new Entry(i,yo));
            yo= random.nextInt(200);
            yorganic.add(new Entry(i,yo));
            yo= random.nextInt(70);

            ycustomer.add(new Entry(i,yo));
            xdata.add(i+1);
        }
        String xaxis[]=new String[xdata.size()];
        for(int i=0;i<xdata.size();i++)
        {
            xaxis[i]= xdata.get(i).toString();
        }
        ArrayList<ILineDataSet> iLineDataSets=new ArrayList<>();
        LineDataSet datasetinorganic=new LineDataSet(yinorganic,"Inorganic");
        datasetinorganic.setDrawCircles(false);
        datasetinorganic.setColor(Color.CYAN);
        datasetinorganic.setLineWidth(3);

        LineDataSet datasetorganic=new LineDataSet(yorganic,"Organic");
        datasetorganic.setDrawCircles(false);
        datasetorganic.setColor(Color.MAGENTA);
        datasetorganic.setLineWidth(3);

        LineDataSet datasetcustomers=new LineDataSet(ycustomer,"No. of customers");
        datasetcustomers.setDrawCircles(false);
        datasetcustomers.setColor(Color.GRAY);
        datasetcustomers.setLineWidth(3);
        iLineDataSets.add(datasetcustomers);
        iLineDataSets.add(datasetinorganic);
        iLineDataSets.add(datasetorganic);
        lineChart.setData(new LineData(iLineDataSets));
        Description desc=new Description();
        desc.setText("Comparison organic inorganic and customers");
        desc.setTextSize(12);
        desc.setTextColor(Color.BLACK);
        lineChart.setDescription(desc);
        lineChart.setVisibleXRangeMaximum(30f);
        lineChart.animateXY(3000,3000);
        lineChart.invalidate();





        addatapie(pieChart);







        return v;
    }

//    private void setupie(PieChart pieChart) {
//
//    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

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
