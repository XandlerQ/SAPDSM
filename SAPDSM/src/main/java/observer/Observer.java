package observer;

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import aviary.Aviary;
import configuration.Agent;
import graph.*;

public class Observer {

    Aviary aviaryReference;
    ArrayList<TimeGraph> timeGraphs;
    int timeGraphCtr;
    int timeGraphCtrPeak;
    int reportRowCtr;

    //Data

    double aviaryData[][];
    int reportCtr;
    int reportCtrPeak;
    ArrayList<String> reportDataHeaders;

    String dataReportFileName;
    String parameterReportFileName;
    String startTimeStamp;
    File reportFile;
    FileWriter fw;
    BufferedWriter bw;
    PrintWriter pw;

    //--------------------------------------
    //-----------  Constructors  -----------
    //--------------------------------------

    Observer() {
        this.aviaryReference = null;
        this.timeGraphs = null;
        this.timeGraphCtr = 0;
        this.timeGraphCtrPeak = configuration.Observer.GRAPHDATACTRPEAK - 1;
        this.reportRowCtr = 0;
        this.aviaryData = null;
        this.reportCtr = 0;
        this.reportCtrPeak = configuration.Observer.REPORTCTRPEAK - 1;
        this.reportDataHeaders = null;
        this.dataReportFileName = null;
        this.parameterReportFileName = null;
        this.startTimeStamp = null;
        this.reportFile = null;
        this.fw = null;
        this.bw = null;
        this.pw = null;
    }

    public Observer(Aviary aviaryReference) {
        this();
        this.aviaryReference = aviaryReference;
        this.timeGraphs = new ArrayList<>();
    }

    Observer(Aviary aviaryReference, int areaCount, int valuesPerArea) {
        this(aviaryReference);
    }

    //--------------------------------------
    //--------------------------------------

    //---------------------------------
    //-----------  Getters  -----------
    //---------------------------------

    public Aviary getAviaryReference() {
        return aviaryReference;
    }

    public int getReportRowCtr() {
        return reportRowCtr;
    }

    //---------------------------------
    //---------------------------------

    //---------------------------------
    //-----------  Setters  -----------
    //---------------------------------

    public void setAviaryReference(Aviary aviaryReference) {
        this.aviaryReference = aviaryReference;
    }

    public void setReportDataHeaders(ArrayList<String> reportDataHeaders) {
        this.reportDataHeaders = reportDataHeaders;
    }

    public void setDataReportFileName(String dataReportFileName) {
        this.dataReportFileName = dataReportFileName;
    }

    public void setParameterReportFileName(String parameterReportFileName) {
        this.parameterReportFileName = parameterReportFileName;
    }
    //---------------------------------
    //---------------------------------

    //---------------------------------
    //-----------  Methods  -----------
    //---------------------------------

    void resetTimeGraphCtr() {
        this.timeGraphCtr = this.timeGraphCtrPeak;
    }

    void resetReportCtr() { this.reportCtr = this.reportCtrPeak; }

    public void fillTimeGraphs() {
        int graphDimX = 495;
        int graphDimY = 200;
        int graphCapacity = 5;

        TimeGraph populationGraph = new TimeGraph(graphDimX, graphDimY, graphCapacity);
        populationGraph.setTitle("Population");
        populationGraph.setOrigin(configuration.Render.ORIGINX + configuration.Aviary.DEFX, 5);
        populationGraph.setPlainCl(new Color(255, 255, 255));
        populationGraph.setBorderCl(new Color(100, 100, 100));
        populationGraph.setDotCl(Color.RED);
        populationGraph.setLineCl(Color.RED);
        populationGraph.setLevelLineCl(Color.RED);
        populationGraph.setValueTextCl(Color.BLACK);
        populationGraph.setTitleTextCl(Color.BLACK);
        populationGraph.setScaleTextCl(Color.BLACK);
        populationGraph.setTextSize(8);
        populationGraph.setInteger(true);

        this.timeGraphs.add(populationGraph);


        ScaleSynchronizer populationScaleSynchronizer = new ScaleSynchronizer();

        TimeGraph populationArea0Graph = new TimeGraph(graphDimX * 2, graphDimY, graphCapacity);
        populationArea0Graph.setTitle("Area population");
        populationArea0Graph.setOrigin(configuration.Render.ORIGINX + configuration.Aviary.DEFX, graphDimY + 5);
        populationArea0Graph.setPlainCl(new Color(0, 0, 0, 0));
        populationArea0Graph.setBorderCl(new Color(100, 100, 100));
        populationArea0Graph.setDotCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[0]);
        populationArea0Graph.setLineCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[0]);
        populationArea0Graph.setLevelLineCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[0]);
        populationArea0Graph.setValueTextCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[0]);
        populationArea0Graph.setScaleTextCl(Color.WHITE);
        populationArea0Graph.setTitleTextCl(Color.WHITE);
        populationArea0Graph.setTextSize(8);
        populationArea0Graph.setInteger(true);
        populationArea0Graph.setScaleSynchronizer(populationScaleSynchronizer);


        TimeGraph populationArea1Graph = new TimeGraph(graphDimX * 2, graphDimY, graphCapacity);
        populationArea1Graph.setTitle("");
        populationArea1Graph.setOrigin(configuration.Render.ORIGINX + configuration.Aviary.DEFX, graphDimY + 5);
        populationArea1Graph.setPlainCl(new Color(0, 0, 0, 0));
        populationArea1Graph.setBorderCl(new Color(100, 100, 100));
        populationArea1Graph.setDotCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[1]);
        populationArea1Graph.setLineCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[1]);
        populationArea1Graph.setLevelLineCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[1]);
        populationArea1Graph.setValueTextCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[1]);
        populationArea1Graph.setTextSize(8);
        populationArea1Graph.setRenderScale(false);
        populationArea1Graph.setRenderTitle(false);
        populationArea1Graph.setInteger(true);
        populationArea1Graph.setScaleSynchronizer(populationScaleSynchronizer);


        populationScaleSynchronizer.addGraph(populationArea0Graph);
        populationScaleSynchronizer.addGraph(populationArea1Graph);

        this.timeGraphs.add(populationArea0Graph);
        this.timeGraphs.add(populationArea1Graph);

        ScaleSynchronizer energyDensityScaleSynchronizer = new ScaleSynchronizer();

        TimeGraph energyDensityArea0Graph = new TimeGraph(graphDimX * 2, graphDimY, graphCapacity);
        energyDensityArea0Graph.setTitle("Area energy density");
        energyDensityArea0Graph.setOrigin(configuration.Render.ORIGINX + configuration.Aviary.DEFX, 2 * graphDimY + 5);
        energyDensityArea0Graph.setPlainCl(new Color(0, 0, 0, 0));
        energyDensityArea0Graph.setBorderCl(new Color(100, 100, 100));
        energyDensityArea0Graph.setDotCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[0]);
        energyDensityArea0Graph.setLineCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[0]);
        energyDensityArea0Graph.setLevelLineCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[0]);
        energyDensityArea0Graph.setValueTextCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[0]);
        energyDensityArea0Graph.setScaleTextCl(Color.WHITE);
        energyDensityArea0Graph.setTitleTextCl(Color.WHITE);
        energyDensityArea0Graph.setTextSize(8);
        energyDensityArea0Graph.setScaleSynchronizer(energyDensityScaleSynchronizer);

        TimeGraph energyDensityArea1Graph = new TimeGraph(graphDimX * 2, graphDimY, graphCapacity);
        energyDensityArea1Graph.setTitle("");
        energyDensityArea1Graph.setOrigin(configuration.Render.ORIGINX + configuration.Aviary.DEFX, 2 * graphDimY + 5);
        energyDensityArea1Graph.setPlainCl(new Color(0, 0, 0, 0));
        energyDensityArea1Graph.setBorderCl(new Color(100, 100, 100));
        energyDensityArea1Graph.setDotCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[1]);
        energyDensityArea1Graph.setLineCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[1]);
        energyDensityArea1Graph.setLevelLineCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[1]);
        energyDensityArea1Graph.setValueTextCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[1]);
        energyDensityArea1Graph.setTextSize(8);
        energyDensityArea1Graph.setRenderScale(false);
        energyDensityArea1Graph.setRenderTitle(false);
        energyDensityArea1Graph.setScaleSynchronizer(energyDensityScaleSynchronizer);

        energyDensityScaleSynchronizer.addGraph(energyDensityArea0Graph);
        energyDensityScaleSynchronizer.addGraph(energyDensityArea1Graph);

        this.timeGraphs.add(energyDensityArea0Graph);
        this.timeGraphs.add(energyDensityArea1Graph);

        ScaleSynchronizer packScaleSynchronizer = new ScaleSynchronizer();

        TimeGraph packArea0Graph = new TimeGraph(graphDimX * 2, graphDimY, graphCapacity);
        packArea0Graph.setTitle("Area pack count");
        packArea0Graph.setOrigin(configuration.Render.ORIGINX + configuration.Aviary.DEFX, 3 * graphDimY + 5);
        packArea0Graph.setPlainCl(new Color(0, 0, 0, 0));
        packArea0Graph.setBorderCl(new Color(100, 100, 100));
        packArea0Graph.setDotCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[0]);
        packArea0Graph.setLineCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[0]);
        packArea0Graph.setLevelLineCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[0]);
        packArea0Graph.setValueTextCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[0]);
        packArea0Graph.setScaleTextCl(Color.WHITE);
        packArea0Graph.setTitleTextCl(Color.WHITE);
        packArea0Graph.setTextSize(8);
        packArea0Graph.setScaleSynchronizer(packScaleSynchronizer);

        TimeGraph packArea1Graph = new TimeGraph(graphDimX * 2, graphDimY, graphCapacity);
        packArea1Graph.setTitle("");
        packArea1Graph.setOrigin(configuration.Render.ORIGINX + configuration.Aviary.DEFX, 3 * graphDimY + 5);
        packArea1Graph.setPlainCl(new Color(0, 0, 0, 0));
        packArea1Graph.setBorderCl(new Color(100, 100, 100));
        packArea1Graph.setDotCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[1]);
        packArea1Graph.setLineCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[1]);
        packArea1Graph.setLevelLineCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[1]);
        packArea1Graph.setValueTextCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[1]);
        packArea1Graph.setTextSize(8);
        packArea1Graph.setRenderScale(false);
        packArea1Graph.setRenderTitle(false);
        packArea1Graph.setScaleSynchronizer(packScaleSynchronizer);

        packScaleSynchronizer.addGraph(packArea0Graph);
        packScaleSynchronizer.addGraph(packArea1Graph);

        this.timeGraphs.add(packArea0Graph);
        this.timeGraphs.add(packArea1Graph);

        ScaleSynchronizer resourceScaleSynchronizer = new ScaleSynchronizer();

        TimeGraph resourceArea0Graph = new TimeGraph(graphDimX * 2, graphDimY, graphCapacity);
        resourceArea0Graph.setTitle("Area resource");
        resourceArea0Graph.setOrigin(configuration.Render.ORIGINX + configuration.Aviary.DEFX, 4 * graphDimY + 5);
        resourceArea0Graph.setPlainCl(new Color(0, 0, 0, 0));
        resourceArea0Graph.setBorderCl(new Color(100, 100, 100));
        resourceArea0Graph.setDotCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[0]);
        resourceArea0Graph.setLineCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[0]);
        resourceArea0Graph.setLevelLineCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[0]);
        resourceArea0Graph.setValueTextCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[0]);
        resourceArea0Graph.setScaleTextCl(Color.WHITE);
        resourceArea0Graph.setTitleTextCl(Color.WHITE);
        resourceArea0Graph.setTextSize(8);
        resourceArea0Graph.setScaleSynchronizer(resourceScaleSynchronizer);

        TimeGraph resourceArea1Graph = new TimeGraph(graphDimX * 2, graphDimY, graphCapacity);
        resourceArea1Graph.setTitle("");
        resourceArea1Graph.setOrigin(configuration.Render.ORIGINX + configuration.Aviary.DEFX, 4 * graphDimY + 5);
        resourceArea1Graph.setPlainCl(new Color(0, 0, 0, 0));
        resourceArea1Graph.setBorderCl(new Color(100, 100, 100));
        resourceArea1Graph.setDotCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[1]);
        resourceArea1Graph.setLineCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[1]);
        resourceArea1Graph.setLevelLineCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[1]);
        resourceArea1Graph.setValueTextCl(configuration.PropertyGrid.PROPERTY_AREA_COLORS[1]);
        resourceArea1Graph.setTextSize(8);
        resourceArea1Graph.setRenderScale(false);
        resourceArea1Graph.setRenderTitle(false);
        resourceArea1Graph.setScaleSynchronizer(resourceScaleSynchronizer);

        resourceScaleSynchronizer.addGraph(resourceArea0Graph);
        resourceScaleSynchronizer.addGraph(resourceArea1Graph);

        this.timeGraphs.add(resourceArea0Graph);
        this.timeGraphs.add(resourceArea1Graph);
    }

    void observeAviaryData() {
        this.aviaryData = aviaryReference.getDataInAreas();
    }

    public void addGraphData() {
        if(this.timeGraphCtr > 0) {
            this.timeGraphCtr--;
        }
        else {
            observeAviaryData();

            this.timeGraphs.get(0).addValue(aviaryReference.getPopulation());
            this.timeGraphs.get(1).addValue(aviaryData[0][0]);
            this.timeGraphs.get(2).addValue(aviaryData[0][1]);

            if (aviaryData[0][0] != 0) this.timeGraphs.get(5).addValue(aviaryData[1][0] / aviaryData[0][0]);
            else this.timeGraphs.get(3).addValue(0);
            if (aviaryData[0][1] != 0) this.timeGraphs.get(6).addValue(aviaryData[1][1] / aviaryData[0][1]);
            else this.timeGraphs.get(4).addValue(0);

            this.timeGraphs.get(5).addValue(aviaryData[2][0]);
            this.timeGraphs.get(6).addValue(aviaryData[2][1]);

            this.timeGraphs.get(7).addValue(aviaryData[3][0]);
            this.timeGraphs.get(8).addValue(aviaryData[3][1]);

            resetTimeGraphCtr();
        }
    }

    public String formRunFolderName() {
        return String.valueOf(configuration.Observer.folderNum);
    }

    public String formTimeStampDataFileName(){
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy_MM_dd__HH_mm_ss");
        Date currentTimeStamp = new Date();
        String timeStampFileName = "report_" + sdfDate.format(currentTimeStamp);

        return timeStampFileName;
    }

    public String formTimeStampParametersFileName(){
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy_MM_dd__HH_mm_ss");
        Date currentTimeStamp = new Date();
        String timeStamp = sdfDate.format(currentTimeStamp);
        String timeStampFileName = "parameters_" + timeStamp;
        this.startTimeStamp = timeStamp;

        return timeStampFileName;
    }

    String formParameterReportString(){
        String reportStr =
                "_____ Parameter Values _____\n\n" +

                        "++++ Aviary Sizes ++++:\n" +
                        "DEFX = " + configuration.Aviary.DEFX + "\n" +
                        "DEFY = " + configuration.Aviary.DEFY + "\n\n" +

                        "+ Resourse Parameters +:\n" +
                        "QUADX = " + configuration.Resource.QUADX + "\n" +
                        "QUADY = " + configuration.Resource.QUADY + "\n" +
                        "PLAINX = " + configuration.Resource.PLAINX + "\n" +
                        "PLAINY = " + configuration.Resource.PLAINY + "\n" +
                        "SHIFTINTERSECTIONMODE = " + configuration.PropertyGrid.SHIFTINTERSECTION + "\n" +
                        "LOCKEDAREAS = " + configuration.PropertyGrid.LOCKEDAREAS + "\n" +
                        "PAYMENT = " + configuration.PropertyGrid.PAYMENT + "\n" +
                        "PAYMENTRATIO = " + configuration.PropertyGrid.PAYMENTRATIO + "\n" +

                        "RESOURCETYPE = " + configuration.Resource.RESTYPE + "\n" +
                        "BASERES = " + configuration.Resource.BASERES + "\n" +
                        "RESREPSPEED = " + configuration.Resource.RESREPSPEED + "\n" +
                        "RESPERQUAD = " + configuration.Resource.RESPERQUAD + "\n" +
                        "RESREPCTRPEAK = " + configuration.Resource.RESREPCTRPEAK + "\n\n" +

                        "++ Agent Parameters ++:\n" +
                        "Initial Agent Spawn:\n" +
                        "INITAGENTAMOUNT1 = " + configuration.Aviary.INITAGENTAMOUNT1 + "\n" +
                        "INITAGENTAMOUNT2 = " + configuration.Aviary.INITAGENTAMOUNT2 + "\n" +
                        "SYSSPAWN = " + configuration.Aviary.SYSSPAWN + "\n\n" +

                        "Speed:\n" +
                        "BASESPEED1 = " + configuration.Agent.BASESPEED1 + "\n" +
                        "BASESPEED2 = " + configuration.Agent.BASESPEED2 + "\n" +
                        "SPEEDAGECOEFF = " + configuration.Agent.SPEEDAGECOEFF + "\n\n" +

                        "Age:\n" +
                        "BASEMAXAGE = " + configuration.Agent.BASEMAXAGE + "\n" +
                        "AGEPERSTEP = " + configuration.Agent.AGEPERSTEP + "\n\n" +

                        "Energy:\n" +
                        "SUFFENERGY = " + configuration.Agent.SUFFENERGY + "\n" +
                        "MAXENERGY = " + configuration.Agent.MAXENERGY + "\n" +
                        "NRGPERSTEP1 = " + configuration.Agent.NRGPERSTEP1 + "\n" +
                        "NRGPERSTEP2 = " + configuration.Agent.NRGPERSTEP2 + "\n\n" +

                        "Valences:\n" +
                        "VALENCE1 = " + configuration.Agent.VALENCE1 + "\n" +
                        "VALENCE2 = " + configuration.Agent.VALENCE2 + "\n\n" +

                        "Resorce Collection:\n" +
                        "RESECOLLECTEDPERSTEP = " + configuration.Agent.RESOURCECOLLECTIONSPEED + "\n\n" +

                        "Reproduction:\n" +
                        "REPRODUCTLOW = " + configuration.Agent.REPRODUCTLOW + "\n" +
                        "REPRODUCTHIGH = " + configuration.Agent.REPRODUCTHIGH + "\n" +
                        "REPRODUCTPROB1 = " + configuration.Agent.REPRODUCTPROB1 + "\n" +
                        "REPRODUCTPROB2 = " + configuration.Agent.REPRODUCTPROB2 + "\n" +
                        "REPRODUCTCOST = " + configuration.Agent.REPRODUCTCOST + "\n\n" +

                        "Fights:\n" +
                        "NRGPERFIGHT = " + configuration.Agent.FIGHTENERGYDRAINSPEED + "\n\n" +

                        "Packs:\n" +
                        "NRGFORCONPERSTEP = " + configuration.Agent.CONNECTIONENERGYDEPLETIONSPEED + "\n\n";

        return reportStr;
    }

    public void parameterReport() {
        String parametersReportStr =
                "======================================================\n" +
                        "===================== Parameters =====================\n" +
                        "======================================================\n\n" +
                        formParameterReportString();

        File parameterReportFile = new File("reports\\" + formRunFolderName() + "\\" + this.parameterReportFileName + ".txt");

        if (!parameterReportFile.exists()) {
            try {
                parameterReportFile.createNewFile();
            }
            catch (IOException e) {
                System.out.println(e);
            }
        }

        try {
            this.fw = new FileWriter(parameterReportFile);
            this.bw = new BufferedWriter(this.fw);
            this.pw = new PrintWriter(this.bw);

            pw.write(parametersReportStr);

            this.pw.close();
            this.bw.close();
            this.fw.close();
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

    public void writeDataHeaders() {
        this.reportFile = new File("reports\\" + formRunFolderName() + "\\" + this.dataReportFileName + ".csv");

        if (!this.reportFile.exists()) {
            try {
                this.reportFile.createNewFile();
            }
            catch (IOException e) {
                System.out.println(e);
            }
        }

        String dataHeadersString;
        StringBuilder stringBuilder = new StringBuilder();

        for (Iterator<String> iterator = this.reportDataHeaders.iterator(); iterator.hasNext();) {
            stringBuilder.append(iterator.next());
            if (iterator.hasNext()) stringBuilder.append(", ");
            else stringBuilder.append("\n");
        }

        dataHeadersString = stringBuilder.toString();

        try {
            this.fw = new FileWriter(this.reportFile, true);
            this.bw = new BufferedWriter(this.fw);
            this.pw = new PrintWriter(this.bw);

            pw.write(dataHeadersString);

            this.pw.close();
            this.bw.close();
            this.fw.close();
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

    public void reportRow() {
        if(this.reportCtr > 0) {
            this.reportCtr--;
        }
        else {
            String reportString;
            StringBuilder stringBuilder = new StringBuilder();
            observeAviaryData();
            stringBuilder.append(aviaryReference.getPopulation()).append(", ");
            stringBuilder.append(aviaryData[0][0]).append(", ");
            stringBuilder.append(aviaryData[0][1]).append(", ");
            stringBuilder.append(aviaryData[1][0] / aviaryData[0][0]).append(", ");
            stringBuilder.append(aviaryData[1][1] / aviaryData[0][1]).append(", ");
            stringBuilder.append(aviaryData[2][0]).append(", ");
            stringBuilder.append(aviaryData[2][1]).append(", ");
            stringBuilder.append(aviaryData[3][0]).append(", ");
            stringBuilder.append(aviaryData[3][1]).append(", ");

            reportString = stringBuilder.toString();

            try {
                this.fw = new FileWriter(this.reportFile, true);
                this.bw = new BufferedWriter(this.fw);
                this.pw = new PrintWriter(this.bw);

                pw.write(reportString);

                this.pw.close();
                this.bw.close();
                this.fw.close();
            }
            catch (IOException e) {
                System.out.println(e);
            }

            resetReportCtr();
            this.reportRowCtr++;
        }
    }

    String formFinalReportString() {
        String finalReportString;
        StringBuilder stringBuilder = new StringBuilder();
        observeAviaryData();
        stringBuilder.append(startTimeStamp).append("  ");
        stringBuilder.append("Base resource ").append(configuration.Resource.BASERES).append("  ");
        stringBuilder.append("Resource replenishment speed multiplier ").append(configuration.Resource.RESREPSPEEDMULTIPLIER).append("\n");
        stringBuilder.append(aviaryData[0][0]).append(", ");
        stringBuilder.append(aviaryData[0][1]).append("\n\n\n");


        finalReportString = stringBuilder.toString();
        return finalReportString;
    }

    public void finalReport() {
        observeAviaryData();
        String finalReportString = formFinalReportString();

        File finalReportFile = new File("reports\\" + formRunFolderName() + "\\" + "FinalReports.txt");

        if (!finalReportFile.exists()) {
            try {
                finalReportFile.createNewFile();
            }
            catch (IOException e) {
                System.out.println(e);
            }
        }


        try {
            this.fw = new FileWriter(finalReportFile, true);
            this.bw = new BufferedWriter(this.fw);
            this.pw = new PrintWriter(this.bw);

            pw.write(finalReportString);

            this.pw.close();
            this.bw.close();
            this.fw.close();
        }
        catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    //---------------------------------
    //---------------------------------

    //-----------------------------------
    //-----------  Renderers  -----------
    //-----------------------------------

    public void render() {
        this.timeGraphs.forEach((timeGraph) -> timeGraph.render());
    }

    //-----------------------------------
    //-----------------------------------


}