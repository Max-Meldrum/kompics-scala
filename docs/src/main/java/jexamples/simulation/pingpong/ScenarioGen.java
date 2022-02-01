package jexamples.simulation.pingpong;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import se.sics.kompics.Init;
import se.sics.kompics.network.Address;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.adaptor.Operation1;
import se.sics.kompics.simulator.adaptor.Operation2;
import se.sics.kompics.simulator.adaptor.distributions.extra.BasicIntSequentialDistribution;
import se.sics.kompics.simulator.events.system.StartNodeEvent;

public class ScenarioGen {

  public static final int PORT = 12345;
  public static final String IP_PREFIX = "192.193.0.";

  static Operation1<StartNodeEvent, Integer> startPongerOp =
      new Operation1<StartNodeEvent, Integer>() {

        @Override
        public StartNodeEvent generate(final Integer self) {
          return new StartNodeEvent() {
            TAddress selfAdr;

            {
              try {
                selfAdr = new TAddress(InetAddress.getByName(IP_PREFIX + self), PORT);
              } catch (UnknownHostException ex) {
                throw new RuntimeException(ex);
              }
            }

            @Override
            public Map<String, Object> initConfigUpdate() {
              HashMap<String, Object> config = new HashMap<>();
              config.put("pingpong.ponger.addr", selfAdr);
              return config;
            }

            @Override
            public Address getNodeAddress() {
              return selfAdr;
            }

            @Override
            public Class<PongerParent> getComponentDefinition() {
              return PongerParent.class;
            }

            @Override
            public Init getComponentInit() {
              return Init.NONE;
            }

            @Override
            public String toString() {
              return "StartPonger<" + selfAdr.toString() + ">";
            }
          };
        }
      };

  static Operation2<StartNodeEvent, Integer, Integer> startPingerOp =
      new Operation2<StartNodeEvent, Integer, Integer>() {

        @Override
        public StartNodeEvent generate(final Integer self, final Integer ponger) {
          return new StartNodeEvent() {
            TAddress selfAdr;
            TAddress pongerAdr;

            {
              try {
                selfAdr = new TAddress(InetAddress.getByName(IP_PREFIX + self), PORT);
                pongerAdr = new TAddress(InetAddress.getByName(IP_PREFIX + ponger), PORT);
              } catch (UnknownHostException ex) {
                throw new RuntimeException(ex);
              }
            }

            @Override
            public Map<String, Object> initConfigUpdate() {
              HashMap<String, Object> config = new HashMap<>();
              config.put("pingpong.pinger.addr", selfAdr);
              config.put("pingpong.pinger.pongeraddr", pongerAdr);
              return config;
            }

            @Override
            public Address getNodeAddress() {
              return selfAdr;
            }

            @Override
            public Class<PingerParent> getComponentDefinition() {
              return PingerParent.class;
            }

            @Override
            public Init getComponentInit() {
              return Init.NONE;
            }

            @Override
            public String toString() {
              return "StartPinger<" + selfAdr.toString() + ">";
            }
          };
        }
      };

  public static SimulationScenario simplePing() {
    SimulationScenario scen =
        new SimulationScenario() {
          {
            SimulationScenario.StochasticProcess ponger =
                new SimulationScenario.StochasticProcess() {
                  {
                    eventInterarrivalTime(constant(1000));
                    raise(5, startPongerOp, new BasicIntSequentialDistribution(1));
                  }
                };

            SimulationScenario.StochasticProcess pinger =
                new SimulationScenario.StochasticProcess() {
                  {
                    eventInterarrivalTime(constant(1000));
                    raise(
                        5,
                        startPingerOp,
                        new BasicIntSequentialDistribution(6),
                        new BasicIntSequentialDistribution(1));
                  }
                };

            ponger.start();
            pinger.startAfterTerminationOf(1000, ponger);
            terminateAfterTerminationOf(10000, pinger);
          }
        };

    return scen;
  }
}
