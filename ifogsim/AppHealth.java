package org.fog.test.perfeval;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.sdn.overbooking.BwProvisionerOverbooking;
import org.cloudbus.cloudsim.sdn.overbooking.PeProvisionerOverbooking;
import org.fog.application.AppEdge;
import org.fog.application.AppLoop;
import org.fog.application.Application;
import org.fog.application.selectivity.FractionalSelectivity;
import org.fog.entities.Actuator;
import org.fog.entities.FogBroker;
import org.fog.entities.FogDevice;
import org.fog.entities.FogDeviceCharacteristics;
import org.fog.entities.Sensor;
import org.fog.entities.Tuple;
import org.fog.placement.Controller;
import org.fog.placement.ModuleMapping;
import org.fog.placement.ModulePlacementEdgewards;
import org.fog.placement.ModulePlacementMapping;
import org.fog.policy.AppModuleAllocationPolicy;
import org.fog.scheduler.StreamOperatorScheduler;
import org.fog.utils.FogLinearPowerModel;
import org.fog.utils.FogUtils;
import org.fog.utils.TimeKeeper;
import org.fog.utils.distribution.DeterministicDistribution;

public class AppHealth {
  static List<FogDevice> fogDevices = new ArrayList<FogDevice>();
  static List<Sensor> sensors = new ArrayList<Sensor>();
  static List<Actuator> actuators = new ArrayList<Actuator>();
  static int numOfAreas = 4;
  static int numOfOximetroPerArea = 12;
  static double OXI_TRANSMISSION_TIME = 5;

  private static boolean CLOUD = true;

  public static void main(String[] args) {
    Scanner s = new Scanner(System.in);
    System.out.println("O PID do programa e:" + ProcessHandle.current().pid());
    System.out.println(System.getProperty("sun.java.command"));
    s.next();

    Log.printLine("E-Health Scenario...");
    try {
      Log.disable();
      int num_user = 1; // number of cloud users
      Calendar calendar = Calendar.getInstance();
      boolean trace_flag = false; // mean trace events

      CloudSim.init(num_user, calendar, trace_flag);

      String appId = "ehs"; // identifier of the application

      FogBroker broker = new FogBroker("broker");

      Application application = createApplication(appId, broker.getId());
      application.setUserId(broker.getId());

      createFogDevices(broker.getId(), appId);

      Controller controller = null;

      ModuleMapping moduleMapping = ModuleMapping.createModuleMapping();

      for (FogDevice device : fogDevices) {
        if (device.getName().startsWith("o")) {
          moduleMapping.addModuleToDevice(
            "processing_center",
            device.getName()
          );
        }
      }

      if (CLOUD) {
        moduleMapping.addModuleToDevice("medical_processing", "cloud"); // placing all instances in the Cloud
      } else {
        for (FogDevice device : fogDevices) {
          if (device.getName().startsWith("f")) {
            moduleMapping.addModuleToDevice(
              "medical_processing",
              device.getName()
            );
          }
        }
      }

      controller =
        new Controller("master-controller", fogDevices, sensors, actuators);

      controller.submitApplication(
        application,
        (CLOUD)
          ? (new ModulePlacementMapping(fogDevices, application, moduleMapping))
          : (
            new ModulePlacementEdgewards(
              fogDevices,
              sensors,
              actuators,
              application,
              moduleMapping
            )
          )
      );

      TimeKeeper
        .getInstance()
        .setSimulationStartTime(Calendar.getInstance().getTimeInMillis());

      CloudSim.startSimulation();

      CloudSim.stopSimulation();

      System.gc();

      Log.printLine("E-Health Scenario finished!");
    } catch (Exception e) {
      e.printStackTrace();
      Log.printLine("Unwanted errors happen");
    }
  }

  /**
   * Creates the fog devices in the physical topology of the simulation.
   * @param userId
   * @param appId
   */
  private static void createFogDevices(int userId, String appId) {
    FogDevice cloud = createFogDevice(
      "cloud",
      44800,
      40000,
      100,
      10000,
      0,
      0.01,
      16 * 103,
      16 * 83.25
    );
    cloud.setParentId(-1);
    fogDevices.add(cloud);
    FogDevice proxy = createFogDevice(
      "proxy-server",
      2800,
      4000,
      10000,
      10000,
      1,
      0.0,
      107.339,
      83.4333
    );
    proxy.setParentId(cloud.getId());
    proxy.setUplinkLatency(100); // latency of connection between proxy server and cloud is 100 ms
    fogDevices.add(proxy);
    for (int i = 0; i < numOfAreas; i++) {
      addArea(i + "", userId, appId, proxy.getId());
    }
  }

  private static FogDevice addArea(
    String id,
    int userId,
    String appId,
    int parentId
  ) {
    FogDevice router = createFogDevice(
      "f-" + id,
      2800,
      4000,
      1000,
      10000,
      2,
      0.0,
      107.339,
      83.4333
    );
    fogDevices.add(router);
    router.setUplinkLatency(2);
    for (int i = 0; i < numOfOximetroPerArea; i++) {
      String mobileId = id + "-" + i;
      FogDevice oximetro = addOximetro(mobileId, userId, appId, router.getId());
      oximetro.setUplinkLatency(2);
      fogDevices.add(oximetro);
    }
    router.setParentId(parentId);
    return router;
  }

  private static FogDevice addOximetro(
    String id,
    int userId,
    String appId,
    int parentId
  ) {
    FogDevice oximetro = createFogDevice(
      "o-" + id,
      500,
      1000,
      10000,
      10000,
      3,
      0,
      87.53,
      82.44
    );
    oximetro.setParentId(parentId);
    Sensor sensor = new Sensor(
      "s-" + id,
      "OXIMETRO",
      userId,
      appId,
      new DeterministicDistribution(OXI_TRANSMISSION_TIME)
    );
    sensors.add(sensor);
    Actuator ptz = new Actuator("ptz-" + id, userId, appId, "OXIMETRO_CONTROL");
    actuators.add(ptz);
    sensor.setGatewayDeviceId(oximetro.getId());
    sensor.setLatency(10.0);
    ptz.setGatewayDeviceId(oximetro.getId());
    ptz.setLatency(1.0);
    return oximetro;
  }

  /**
   * Creates a vanilla fog device
   * @param nodeName name of the device to be used in simulation
   * @param mips MIPS
   * @param ram RAM
   * @param upBw uplink bandwidth
   * @param downBw downlink bandwidth
   * @param level hierarchy level of the device
   * @param ratePerMips cost rate per MIPS used
   * @param busyPower
   * @param idlePower
   * @return
   */
  private static FogDevice createFogDevice(
    String nodeName,
    long mips,
    int ram,
    long upBw,
    long downBw,
    int level,
    double ratePerMips,
    double busyPower,
    double idlePower
  ) {
    List<Pe> peList = new ArrayList<Pe>();

    // 3. Create PEs and add these into a list.
    peList.add(new Pe(0, new PeProvisionerOverbooking(mips))); // need to store Pe id and MIPS Rating

    int hostId = FogUtils.generateEntityId();
    long storage = 1000000; // host storage
    int bw = 10000;

    PowerHost host = new PowerHost(
      hostId,
      new RamProvisionerSimple(ram),
      new BwProvisionerOverbooking(bw),
      storage,
      peList,
      new StreamOperatorScheduler(peList),
      new FogLinearPowerModel(busyPower, idlePower)
    );

    List<Host> hostList = new ArrayList<Host>();
    hostList.add(host);

    String arch = "x86"; // system architecture
    String os = "Linux"; // operating system
    String vmm = "Xen";
    double time_zone = 10.0; // time zone this resource located
    double cost = 3.0; // the cost of using processing in this resource
    double costPerMem = 0.05; // the cost of using memory in this resource
    double costPerStorage = 0.001; // the cost of using storage in this
    // resource
    double costPerBw = 0.0; // the cost of using bw in this resource
    LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN
    // devices by now

    FogDeviceCharacteristics characteristics = new FogDeviceCharacteristics(
      arch,
      os,
      vmm,
      host,
      time_zone,
      cost,
      costPerMem,
      costPerStorage,
      costPerBw
    );

    FogDevice fogdevice = null;
    try {
      fogdevice =
        new FogDevice(
          nodeName,
          characteristics,
          new AppModuleAllocationPolicy(hostList),
          storageList,
          10,
          upBw,
          downBw,
          0,
          ratePerMips
        );
    } catch (Exception e) {
      e.printStackTrace();
    }

    fogdevice.setLevel(level);
    return fogdevice;
  }

  private static Application createApplication(String appId, int userId) {
    Application application = Application.createApplication(appId, userId); // creates an empty application model (empty directed graph)

    /*
     * Adding modules (vertices) to the application model (directed graph)
     */
    application.addAppModule("processing_center", 10);
    application.addAppModule("medical_processing", 10);

    /*
     * Connecting the application modules (vertices) in the application model (directed graph) with edges
     */
    application.addAppEdge(
      "OXIMETRO",
      "processing_center",
      1000,
      500,
      "OXIMETRO",
      Tuple.UP,
      AppEdge.SENSOR
    );
    application.addAppEdge(
      "processing_center",
      "medical_processing",
      1000,
      500,
      "OXIMETRO_DATA",
      Tuple.UP,
      AppEdge.MODULE
    );
    application.addAppEdge(
      "medical_processing",
      "OXIMETRO_CONTROL",
      100,
      28,
      100,
      "OXIMETRO_PARAMS",
      Tuple.UP,
      AppEdge.ACTUATOR
    );

    /*
     * Defining the input-output relationships (represented by selectivity) of the application modules.
     */
    application.addTupleMapping(
      "processing_center",
      "OXIMETRO",
      "OXIMETRO_DATA",
      new FractionalSelectivity(1.0)
    );
    application.addTupleMapping(
      "medical_processing",
      "OXIMETRO_DATA",
      "OXIMETRO_PARAMS",
      new FractionalSelectivity(1.0)
    );

    final AppLoop loop1 = new AppLoop(
      new ArrayList<String>() {

        {
          add("OXIMETRO");
          add("processing_center");
          add("medical_processing");
          add("OXIMETRO_CONTROL");
        }
      }
    );
    List<AppLoop> loops = new ArrayList<AppLoop>() {

      {
        add(loop1);
      }
    };
    application.setLoops(loops);

    return application;
  }
}
