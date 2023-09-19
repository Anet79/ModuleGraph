package com.anet.graphmodule;

import static com.anet.graphmodule.R.id.pie_chart_1;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Graph graph_view = findViewById(R.id.graph_view_1);
        PieChartView pie_chart = findViewById(R.id.pie_chart);
        PieChartView pie_chart_1 = findViewById(R.id.pie_chart_1);
        List<DataModel> dataList = new ArrayList<>();

        pie_chart.addItemType(new PieChartView.ItemType("One", 25,Color.parseColor("#461959") ));
        pie_chart.addItemType(new PieChartView.ItemType("Two", 17,Color.parseColor("#7A316F")));
        pie_chart.addItemType(new PieChartView.ItemType("Three", 13,Color.parseColor("#CD6688")));
        pie_chart.addItemType(new PieChartView.ItemType("four", 8, Color.parseColor("#AED8CC")));
        pie_chart.setCell(5);
       // pie_chart.setInnerRadius(0.4f);

        pie_chart_1.addItemType(new PieChartView.ItemType("One", 25,Color.parseColor("#461959") ));
        pie_chart_1.addItemType(new PieChartView.ItemType("Two", 17,Color.parseColor("#7A316F")));
        pie_chart_1.addItemType(new PieChartView.ItemType("Three", 13,Color.parseColor("#CD6688")));
        pie_chart_1.addItemType(new PieChartView.ItemType("four", 8, Color.parseColor("#AED8CC")));
        pie_chart_1.setCell(5);
        pie_chart_1.setInnerRadius(0.4f);

        LinearChartView linearChartView = findViewById(R.id.linearChartView);

        List<LinearChartView.ItemType> itemTypeList = new ArrayList<>();
        linearChartView.addItemType(new LinearChartView.ItemType("One", 50, Color.parseColor("#CD6688")));
        linearChartView.addItemType(new LinearChartView.ItemType("Two", 20, Color.parseColor("#461959")));
        linearChartView.addItemType(new LinearChartView.ItemType("Three", 30,Color.parseColor("#7A316F")));


        graph_view.addItemType(new Graph.ItemType("One", 50, Color.parseColor("#CD6688")));
        graph_view.addItemType(new Graph.ItemType("Two", 20, Color.parseColor("#461959")));
        graph_view.addItemType(new Graph.ItemType("Three", 30,Color.parseColor("#7A316F")));






        List<Float> dataValues = new ArrayList<>();
        dataValues.add(30f);
        dataValues.add(50f);
        dataValues.add(20f);

        List<Integer> sliceColors = new ArrayList<>();
        sliceColors.add(Color.RED);
        sliceColors.add(Color.GREEN);
        sliceColors.add(Color.BLUE);

      //  pie_chart.setData(dataValues, sliceColors);
    }


}