/*-
 * ============LICENSE_START=======================================================
 * ACTN tool - Topo Generator
 * ================================================================================
 * Copyright (C) 2021 Huawei Technologies Co., Ltd. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
"use strict";
Draw.loadPlugin(function (ui) {
  const { app, dialog, BrowserWindow } = require("electron").remote;
  const win = BrowserWindow.getFocusedWindow();
  const fs = require("fs");
  const path = require("path");
  const ongenDir = path.dirname(app.getPath("exe")) + "/ongen/";
  var graph = ui.editor.graph;
  var model = ui.editor.graph.model;
  const tsNumbers = new Map([
    ["ODU1", 2],
    ["ODU2", 8],
    ["ODU3", 32],
    ["ODU4", 80]
  ]);
  const getBitmap = (ts) =>
    Buffer.alloc(Math.ceil(ts / 8), 255).toString("base64");
  const btoa = (b) => Buffer.from(b, "utf8").toString("base64");
  const http = require("http");

  // Adds resource and menu for action
  mxResources.parse("actnJSON=Export to ACTN JSON files");
  mxResources.parse("initPNC=Initialize PNC Simulators");
  mxResources.parse("collapseAll=Collapse Containers");
  mxResources.parse("expandAll=Expand Containers");
  mxResources.parse("assignNodeId=Auto-assign Node IDs");
  mxResources.parse("loadSetting=Load Default Settings");
  mxResources.parse("saveSetting=Save Current Settings");
  var menu = ui.menus.get("extras");
  var oldFunct = menu.funct;
  menu.funct = function (menu, parent) {
    oldFunct(menu, parent);
    ui.menus.addMenuItems(
      menu,
      [
        "-",
        "actnJSON",
        "initPNC",
        "collapseAll",
        "expandAll",
        "assignNodeId",
        "loadSetting"
      ],
      parent
    );
  };

  // Adds action of collpaseAll
  ui.actions.addAction(
    "collapseAll",
    function () {
      var cells = model.filterDescendants(
        (cell) => !!cell.getAttribute("te_node_id")
      );
      var nodeAllCollapsed = true;
      cells.forEach((cell) => (nodeAllCollapsed &= cell.collapsed));
      if (!nodeAllCollapsed) {
        graph.cellsFolded(cells, true, false, true);
      } else {
        graph.selectVertices(null, true);
        graph.cellsFolded(graph.getSelectionCells(), true, false, true);
        graph.clearSelection();
      }
    },
    null,
    null,
    "Ctrl+J"
  );
  ui.keyHandler.bindAction(74, !0, "collapseAll", !1);

  // Adds action of expandAll
  ui.actions.addAction(
    "expandAll",
    function () {
      graph.selectVertices(null, true);
      graph.cellsFolded(graph.getSelectionCells(), false, false, true);
      graph.clearSelection();
    },
    null,
    null,
    "Ctrl+K"
  );
  ui.keyHandler.bindAction(75, !0, "expandAll", !1);

  // Adds action of loadSetting
  ui.actions.addAction("loadSetting", function () {
    var plugins = mxSettings.getPlugins();
    mxSettings.parse(DEFAULT_SETTING);
    mxSettings.setPlugins(plugins);
    mxSettings.save();

    ui.setDatabaseItem(
      null,
      [
        {
          title: ".scratchpad",
          size: SCRATCHPAD.length,
          lastModified: Date.now(),
          type: "L"
        },
        { title: ".scratchpad", data: SCRATCHPAD }
      ],
      null,
      null,
      ["filesInfo", "files"]
    );
    notify("Restart the App to take effect.");
  });

  // Adds hidden action with CTRL+H
  ui.actions.addAction("saveSetting", function () {
    win.webContents.toggleDevTools();
    fs.writeFileSync(
      ongenDir + "default-settings.json",
      JSON.stringify(mxSettings.settings)
    );
  });
  ui.keyHandler.bindAction(72, !0, "saveSetting", !1);

  // Adds action for actn JSON
  ui.actions.addAction("actnJSON", function () {
    var domains = generateJson();
    if (!domains) {
      return;
    }
    //Get file base name
    var fileDir = dialog.showOpenDialogSync(win, {
      title: "Choose A Directory to Save ACTN JSON Data Files",
      buttonLabel: "Yes",
      properties: ["openDirectory"]
    });
    if (!fileDir) {
      return;
    }

    domains.forEach(function (domain) {
      var fileName = fileDir + "/" + (domain._port_ || domain.te_name);
      fs.writeFileSync(fileName + "{otn-topo}.json", domain._data_);
    });

    dialog.showOpenDialogSync(win, {
      title: "Generated ACTN JSON Data Files",
      defaultPath: fileDir + "/*",
      buttonLabel: "Ok",
      filters: [{ name: "DATA", extensions: ["json", "xlsx"] }],
      properties: ["multiSelections", "dontAddToRecent"]
    });
  });

  // Adds action for initializing PNC simulators
  ui.actions.addAction("initPNC", function () {
    var domains = generateJson();
    if (!domains) {
      return;
    }
    //Send PUT messages to each PNC simulator
    var reqNum = 0;
    domains.forEach(function (domain) {
      reqNum++;
      const options = {
        host: domain._ip_,
        port: parseInt(domain._port_),
        path: "/pncsimu/v1/reload-data",
        method: "POST",
        headers: { "Content-Type": "application/json" }
      };
      const req = http.request(options, function (res) {
        if (res.statusCode != 201 && res.statusCode != 200) {
          notify(
            `${res.statusCode} Error from ${options.host}:${options.port}`
          );
          return res.resume();
        }
        if (!--reqNum) {
          notify(`${domains.length} PNC simulators initialized`);
        }
      });
      req.write(domain._data_);
      req.end();
    });
  });

  // Adds action: Auto assign node id of all nodes
  ui.actions.addAction("assignNodeId", function () {
    var domains = model.filterDescendants(
      (cell) => !!cell.getAttribute("network_types")
    );
    var i = 1;
    graph.clearSelection();
    model.beginUpdate();
    domains.forEach(function (domain) {
      var nodes = model.getChildVertices(domain);
      var j = 1;
      var k = 1;
      nodes.forEach(function (node) {
        if (!node.hasAttribute("te_node_id")) {
          return;
        }
        var newXmlValue = node.value.cloneNode(false);
        newXmlValue.setAttribute("te_node_id", "10." + i + "." + j + "." + k);
        model.setValue(node, newXmlValue);
        graph.addSelectionCell(node);
        if (k < 254) {
          k++;
        } else {
          k = 1;
          j++;
        }
      });
      i++;
    });
    model.endUpdate();
  });

  function generateJson() {
    //prepare js object arrays for domains, nodes, TP&links
    var domainNetworks = [];
    var domains = model.filterDescendants(
      (cell) => !!cell.getAttribute("network_types")
    );
    var domainMap = new Map();
    graph.clearSelection();
    try {
      domains.forEach(function (domain) {
        checkObjId("Domain", "te_name", domainMap, domain);
        var otnTopo = xml2js(domain.value);
        var ethTopo = { ...otnTopo };
        ethTopo["network_types"] = "eth-tran-topology";

        var nodes = model.getChildVertices(domain);
        var nodeMap = new Map();
        (otnTopo.nodes = []), (ethTopo.nodes = []);
        (otnTopo.links = []), (ethTopo.links = []);
        nodes.forEach(function (node) {
          checkObjId("Node", "te_node_id", nodeMap, node);
          let otnNode = xml2js(node.value);
          let ethNode = { ...otnNode };
          (otnNode.tps = []), (ethNode.tps = []);
          otnTopo.nodes.push(otnNode);
          ethTopo.nodes.push(ethNode);

          let tps = model.getChildVertices(node);
          let tpMap = new Map();
          tps.forEach(function (tp) {
            checkObjId("TP", "te_tp_id", tpMap, tp);
            let newTpLink = xml2js(tp.value);
            newTpLink["te_node_id"] = otnNode["te_node_id"];
            if (newTpLink["otn_odu_type"])
              newTpLink.ts_number =
                tsNumbers.get(newTpLink["otn_odu_type"]) || 0;
            let links = model.getConnections(tp);
            let inniLinkNumber = 0,
              enniLinkNumber = 0;
            for (let link of links) {
              if (link.hasAttribute("te_inter_domain_plug_id")) {
                if (!link.getAttribute("te_inter_domain_plug_id")) {
                  link.setAttribute(
                    "te_inter_domain_plug_id",
                    link.getId().substr(-8)
                  );
                }
                enniLinkNumber++;
              } else if (link.hasAttribute("te_delay_metric")) {
                inniLinkNumber++;
              } else {
                notify("unknown link!");
                graph.addSelectionCell(link);
                return;
              }
            }
            if (inniLinkNumber + enniLinkNumber > 1) {
              notify("NNI TP has more than one link!");
              graph.addSelectionCell(tp);
              return;
            }
            if (newTpLink["otn_odu_type"] && links.length == 1) {
              xml2js(links[0].value, newTpLink);
              newTpLink.bitmap = getBitmap(newTpLink.ts_number);
              let dest_tp = model.getOpposites(links, tp, true, true);
              if (dest_tp.length != 0) {
                if (
                  newTpLink["otn_odu_type"] !=
                  dest_tp[0].getAttribute("otn_odu_type")
                ) {
                  notify(
                    "The two TPs of the link don't have the same odu_type!"
                  );
                  graph.addSelectionCell(tp);
                  graph.addSelectionCell(links[0]);
                  graph.addSelectionCell(dest_tp[0]);
                  return;
                }
                let dest_node = dest_tp[0].parent;
                if (
                  dest_node != null &&
                  dest_node != undefined &&
                  node.parent == dest_node.parent
                ) {
                  newTpLink["dest_node"] = dest_node.getAttribute("te_node_id");
                  newTpLink["dest_tp"] = dest_tp[0].getAttribute("te_tp_id");
                }
              }
            }

            if (newTpLink["otn_odu_type"]) {
              otnNode.tps.push(newTpLink);
              otnTopo.links.push(newTpLink);
            } else if (newTpLink["eth_bandwidth"]) {
              ethNode.tps.push(newTpLink);
              ethTopo.links.push(newTpLink);
            }
          });
        });

        //generate JSON data for each domain
        let jsonData = jjsRender(ONGEN_TMPL, {
          networks: [otnTopo, ethTopo],
          btoa: btoa
        });
        otnTopo._data_ = JSON.stringify(JSON.parse(jsonData), null, 3);
        domainNetworks.push(otnTopo);
      });
    } catch (e) {
      return notify(e.message);
    }
    if (domainNetworks.length == 0) return notify("No domain network exists");
    return domainNetworks;
  }

  function notify(message) {
    dialog.showMessageBoxSync(win, { title: "ongen", message: message });
  }

  //convert data from xml node to js object
  function xml2js(xmlNode, jsObj = {}) {
    for (let attr of xmlNode.attributes) {
      if (attr.name != "label" && attr.name != "placeholders") {
        attr.value = attr.value.replace(/(^\s*)|(\s*$)/gm, "");
        jsObj[attr.name] = attr.value;
      }
    }
    return jsObj;
  }

  //check the id of a new object: null, undefined or conflicts with other's
  function checkObjId(objName, objIdName, objMap, newObj) {
    let newObjValue = newObj.getAttribute(objIdName);
    if (!newObjValue) {
      graph.addSelectionCell(newObj);
      throw new Error(objName + " " + objIdName + " is missing!");
    }
    if (objMap.has(newObjValue)) {
      graph.addSelectionCell(newObj);
      graph.addSelectionCell(objMap.get(newObjValue));
      throw new Error("Same " + objName + " " + objIdName + "!");
    }
    objMap.set(newObjValue, newObj);
    return true;
  }

  function jjsRender(tmpl, data, debug = false) {
    // generate variable part
    var i = 2,
      res = [""];
    Object.keys(data).forEach((k) => (res[0] += `var ${k} = __data['${k}'];`));
    res[1] = "var __i = 0, __res = []\n__res[__i++] = `";

    // generate body part
    var sRegex = /<</g,
      eRegex = />>/g;
    while (sRegex.test(tmpl)) {
      res[i++] = tmpl.slice(eRegex.lastIndex, sRegex.lastIndex - 2);
      eRegex.lastIndex = sRegex.lastIndex;
      if (!eRegex.test(tmpl)) throw new Error(`>> not found.`);
      res[++i] = tmpl.slice(sRegex.lastIndex, eRegex.lastIndex - 2);
      res[i - 1] = /[\{\}]/.test(res[i++]) ? "`\n" : "`\n__res[__i++] = ";
      res[i++] = "\n__res[__i++] = `";
      sRegex.lastIndex = eRegex.lastIndex;
    }
    res[i++] = tmpl.slice(eRegex.lastIndex);

    // generate return part
    res[i] = "`\nreturn __res.join('')";
    if (debug) return res.join("");
    return Function("__data", res.join(""))(data);
  }
});

const DEFAULT_SETTING = `{"language":"","configVersion":null,"customFonts":[],"libraries":"","customLibraries":["L.scratchpad"],"recentColors":["999999","B3B3B3"],"formatWidth":240,"createTarget":false,"pageFormat":{"x":0,"y":0,"width":1169,"height":827},"search":true,"showStartScreen":true,"gridColor":"#d0d0d0","darkGridColor":"#6e6e6e","autosave":false,"resizeImages":null,"openCounter":272,"version":18,"unit":1,"isRulerOn":false,"ui":"dark"}`;
const SCRATCHPAD = `<mxlibrary>[{"xml":"dVTBbtswDP0aHzvIctI0xyXtusM2FN1hR0O2OVurLAoyUyf7+lGynDjBmiAAxfdIPlJUsmLfH5+9ct13bMBkxVNW7D0iTVZ/3IMxmRS6yYrHTErBv0x++QDNIyqc8mDpPwFY/YGamGFUFYo9RnBNUFrVA1uZvDccuKs8W22w2Nlgr7QtuUgghAJzQEzw9aBG0Hc/gEb0b0mBUTV0aBrww5U0O7FKOjlICJK9I3RosD1NpNKhp3KWFzwXCVMykYjaJVouN58Ef1OZmRjDr2c10Mkk5UOnXDB5XsoYYAFe9dyzA697IPC32MsF2I2dJvjpuE9mjXyD7OuoD1PN2fytjdlzTyGHRcuk3aD/Bi7LvF+vN8VmI1bb/EEU8oHBd/Cka2U+G91aZlVIhEFMvKoXHDRpDEDNVxsFzBHfbgg8S0ZVynOm12iJZxibyqez5WVQVZyGYA93pp4aTYngoT74Qb/DK8zKQxPk8Q1+6Ya6xJs8c6+ZLLbxE3cn3EXQCccPFzRfXNEzIIvwvAZiTBUYXRVp7TrQbZfC5CrtgEpr1J5jz+leQ3+25QalSAK2Keo0L1I6L8qdfVflbqopw1O1imCHB9sMy0fGxqKTiyvu33xML3E+Xl78FL38Q/gH","w":435,"h":240,"aspect":"fixed","title":"Network"},{"xml":"dVPRbsIwDPyavKKQCtBeKYyXTZrYB6C08dqgNKlSA2VfP6dJoUwgtZJ9d87VdsqyvOl3Xrb1p1NgWLZlWe6dwxg1fQ7GMMG1YtmGCcHpZeL9BTsfWN5KDxafFLjiCCWSwsgimG0GcoFwsLIBiphYGipcF56iKkSJpo87kEtQDA5GllA7o8B3D9YTbYT5LD13PlgNZFBGeJSLkD721uHVJD0Buu0oWV9qjfDd0kcQc6HxEVZjE1qaU/ijjcmdcZ5y62yoKJ1FqS34JPFQnnynz7CHTv+GY3hUWZqQLMyINIByqzSmMus+0ugCOYzxSyqlbZUg6FtpVUpic2fwCP3LDc0nPe/AkaG/kuSiFdZR8RaXyGvQVY2PmEzzr26Vt8P2oRNbUSuCj/arVHZN817MVmmlE7/lEz/xz08aBG8lwtqdrOqmt4yCSSd3aFjomKarOKb3Kx+rp3/EHw==","w":90,"h":90,"aspect":"fixed","title":"Node"},{"xml":"dVJNb4MwDP01OXYK4aPsuEHX07TDtDNKwZRsAaPgjvLvF0jaUmklQkree36OHbMwa897I/vmHSvQLNyxMDOI5HbtOQOtmeCqYmHOhOD2Z+LtARssLO+lgY7+CcDDN5RkFVoe5mT5QsYEBfWFNRGxN9CyhAZ1BWa4c74qHSjWaCdb8PjGL0cjdQVWp4Km3gs+8q/oxpVa2esWtSxVd3SCWuoBnOKSbMl1X/ZAk/aOBk9dBbM0YOHr2CiCz94WYYHRdtdiDbXa07XSOkONZokNEy6fg63FBzL4AytG5NuE8zkCO1rh9fJZvAWSu0qRN0abUtFkT/EcJbXG8cUYHOcuzogr6RcMwfnhkwWrSveANoeZrGRybPyUpFEYBWma8NQuAZvAN3NUFTVOFfqnaUAdG+8r+FPkldI/6/FqfxsSu/Edvhz92FyOt/FcpHfT+wc=","w":32,"h":20.44,"aspect":"fixed","title":"OTN NNI"},{"xml":"dVJNb4MwDP01uVZ8qEM9rtD2NGm3bacqEAuyBVIFt4V/PyeEQrQVCcnPz89fMUvzdjgZfmnetADF0gNLc6M1TlY75KAUSyIpWFqwJInoZ8nxCRs7NrpwAx3+I9DlN1RIEYqXtljhSCdRvIJGKwGmD3IhnKkvPp5bQCOrgJvLuhxhRz2OCiYWOvFqjL4T6nRHzn2DrS0ek0nkUSqLIkLV1dxAeKpHo3/gQwpsyEMF9tQCPwiJPhrI5KUr41INEj9X9pcN22w9KoZZZcHowTQHiBqeLjBeDXcCbdcwUsjd90Xsdtpx1ICsGwx9hnaH8ham537F9SPdo8K7lp19oNG/3ibLdsuXJVmYoddXU4EXrQ9jzjNMcS+7UIfc1IB/dGSshlxc7lFn6I9ohsuxTur1Lf8C","w":69,"h":1,"aspect":"fixed","title":"INNI Link"},{"xml":"dVNNb4MwDP01XCcKarXrSj9Ok3Zbd0KBGMgWSGXcFv79TEjaoK0HJD8/28+xTZRm7XBEcW7ejQQdpfsozdAYmq12yEDrKImVjNJdlCQxf1FyeMKuLBufBUJH/ySY4htK4ggtiklsZ0mbokUJjdESsF/UIshVR4C5NK1QXX7Wlzr3enOIRxYuG+tp1DCz0Mk3RHNj1JmOnduG2qmHFZtMHpSeUMyovOAVpKN6QvMDn0pSwx4W2LZAYi8VuWhgUxRWxpYaFJ0C+2sKe1k7tBt81gRGByrWzow2aFtNq6pKyvKuHTByU2zWG9sw4XjytSYQ6EzwIWSRV5onBrKGpxtbBWM8guHX4sghNzcBZtfzUuMGVN3Q0oegBanrsrxwO63v5e4KH4bXG4T05oIlOG94aj5wcJWT12VtElgD/UlkI3jGw2UPxEN3lx4+7n/ODn+PXw==","w":128,"h":1,"aspect":"fixed","title":"ENNI Link"},{"xml":"dVJNb8IwDP01OW5KEzF2XmGcph2mnavQuDQsjatgBv33c5sARRqNKsXvPdvxh9Bld95E07cfaMELvRa6jIiUbt25BO+Fks4KvRJKSf6Fen/AFhMrexMh0D8OuN1DTazwZjsmW03kgqCivuIgapEDeFNDi95CPNxFvionUBVqDgfTQVY/5ZNopFChPVY09FnwufpWN672jt9bNaZ2YZcEFI+QBNdko3lf9oEGnwNGPAYLo7QQ+u3UOoKvnotg4MTdZaylzme6cd6X6DFOvvrFSNk0jB8o4g/MGL2UsixHDww0w5vpY7wDMmvrKAdGTuloYGsh2awxBO632U6vHJFU0i9EgvPDkRWzSjeAnCMOLDk5S21S6DRV2YLbtdlLyefXZYJNHtru6nxbAb7k/l3MvBQX87Z8k/RuN/8A","w":30,"h":20.87,"aspect":"fixed","title":"OTN UNI"},{"xml":"dVLbbsIwDP2aPIJyaVFfRwc87WkfgFJiSLc0qYI36N/PTQMUaSSKFB8fH8eOmaq76y7q3n4EA46pDVN1DAGnW3etwTkmeWuYemdScjpMbl94RfLyXkfw+E9AaL7ggMRwuhmTvSdnibDHfk8isswCTh/ABmcgnp+U78wElkLOYa87yOxF3pMb0O4b7c2lNWgzg09rItwEk9xzaWccXFaN4ccbGKmCqfXFtgifPT2UgAt1kDCLncvuY+tcHVyIKVaJRgsg7fUZY/iGmYfz1eZtO0YEjzP8mBbhHaDemBazcKCULQ5klZzMQ/Ceeqqb9MoRmUr6hYhwffktYlbpDgLliANRhtzY5aoqVCGqasUr2hIWophEZj1UuXsW2pPNupIvi8zU+etOd/nHINAld/hm5tG4mY8RTNSnCf0D","w":30,"h":20.44,"aspect":"fixed","title":"ETH UNI"}]</mxlibrary>`;

const ONGEN_TMPL = `
{
    "ietf-network:networks": {
        "network": [
            << networks.forEach((network, i)=> { >>
                << i ? ',' : '' >>
            {
                "network-id": "providerId-<< network.domain_id >>-clientId-0-topologyId-<< i+1 >>",
                "ietf-te-topology:te": {
                    "name": "<< network.te_name >>"
                },
                "ietf-te-topology:provider-id": << network.domain_id >>,
                "ietf-te-topology:client-id": 0,
                "ietf-te-topology:te-topology-id": "<< i+1 >>",
                "network-types": {
                    "ietf-te-topology:te-topology": {
                        << if (network.network_types == "otn-topology") { >>
                        "ietf-otn-topology:<< network.network_types >>": {}
                        << } else if (network.network_types == "eth-tran-topology") { >>
                        "ietf-eth-te-topology:<< network.network_types >>": {}
                        << } >>
                    }
                },
                "node": [
                    << network.nodes.forEach((node, i)=> { >>
                        << i ? ',' : '' >>
                    {
                        "node-id": "<< node.te_node_id >>",
                        "ietf-te-topology:te-node-id": "<< node.te_node_id >>",
                        "ietf-network-topology:termination-point": [
                            << node.tps.forEach((tp, i)=> { >>
                                << i ? ',' : '' >>
                            {
                                "tp-id": "<< tp.te_tp_id >>",
                                "ietf-te-topology:te-tp-id": << tp.te_tp_id >>,
                                << if (tp.eth_bandwidth) { >>
                                "ietf-eth-te-topology:svc": {
                                    "client-facing": true,
                                    "supported-classification": {
                                        "port-classification": true,
                                        "vlan-classification": {
                                            "outer-tag": {
                                                "supported-tag-types": [
                                                    "ietf-eth-tran-types:classify-c-vlan",
                                                    "ietf-eth-tran-types:classify-s-vlan"
                                                ],
                                                "vlan-bundling": false,
                                                "vlan-range": "1-4094"
                                            }
                                        }
                                    }
                                },
                                << } >>
                                "ietf-te-topology:te": {
                                    "name": "<< tp.te_tp_name >>",
                                    "admin-status": "up",
                                    "oper-status": "up",
                                    << if (tp.te_inter_domain_plug_id) { >>
                                    "inter-domain-plug-id": "<< btoa(tp.te_inter_domain_plug_id) >>",
                                    << } >>
                                    << if (tp.otn_odu_type) { >>
                                    "ietf-otn-topology:client-facing": << tp.otn_client_facing >>,
                                    "interface-switching-capability": [
                                        {
                                            "encoding": "ietf-te-types:lsp-encoding-oduk",
                                            "switching-capability": "ietf-te-types:switching-otn",
                                            "max-lsp-bandwidth": [
                                                {
                                                    "priority": 7,
                                                    "te-bandwidth": {
                                                        "ietf-otn-topology:odu-type": "ietf-otn-types:prot-<< tp.otn_odu_type>>"
                                                    }
                                                }
                                            ]
                                        }
                                    ]
                                    << } >>
                                    << if (tp.eth_bandwidth) { >>
                                    "interface-switching-capability": [
                                        {
                                            "encoding": "ietf-te-types:lsp-encoding-ethernet",
                                            "switching-capability": "ietf-te-types:switching-l2sc",
                                            "max-lsp-bandwidth": [
                                                {
                                                    "priority": 7,
                                                    "te-bandwidth": {
                                                        "ietf-eth-te-topology:eth-bandwidth": << tp.eth_bandwidth >>
                                                    }
                                                }
                                            ]
                                        }
                                    ]
                                    << } >>
                                }
                            }
                            << }) >>
                        ],
                        "ietf-te-topology:te": {
                            "oper-status": "up",
                            "te-node-attributes": {
                                "admin-status": "up",
                                "name": "<< node.te_name >>"
                            }
                            << if (network.network_types == "otn-topology") { >>,
                            "tunnel-termination-point": [
                                << let $i = 0; node.tps.forEach((tp, i)=> { >>
                                << if (tp.otn_client_facing == "false") { >>
                                    << $i ? ',' : ''; $i = 1 >>
                                {
                                    "tunnel-tp-id": "<< btoa(tp.te_tp_id) >>",
                                    "admin-status": "up",
                                    "oper-status": "up",
                                    "encoding": "ietf-te-types:lsp-encoding-oduk",
                                    "name": "<< tp.te_tp_name >>",
                                    "protection-type": "ietf-te-types:lsp-protection-unprotected",
                                    "switching-capability": "ietf-te-types:switching-otn",
                                    "local-link-connectivities": {
                                        "local-link-connectivity": [
                                            {
                                                "is-allowed": true,
                                                "link-tp-ref": "<< tp.te_tp_id >>"
                                            }
                                        ]
                                    }
                                }
                                << } }) >>
                            ]
                            << } >>
                        }
                    }
                    << }) >>
                ],
                "ietf-network-topology:link": [
                    << network.links.forEach((link, i)=> { >>
                        << i ? ',' : '' >>
                    {
                        "link-id": "<< link.te_node_id >>-<< link.te_tp_id >>",
                        "source": {
                            "source-node": "<< link.te_node_id >>",
                            "source-tp": "<< link.te_tp_id >>"
                        },
                        << if (link.dest_node) { >>
                        "destination": {
                            "dest-node": "<< link.dest_node >>",
                            "dest-tp": "<< link.dest_tp >>"
                        },
                        << } >>
                        "ietf-te-topology:te": {
                            "oper-status": "up",
                            "te-link-attributes": {
                                "access-type": "point-to-point",
                                "admin-status": "up",
                                "name": "<< link.te_node_id >>-<< link.te_tp_id >>",
                                << if (link.te_delay_metric) { >>
                                "te-delay-metric": << link.te_delay_metric >>,
                                << } >>
                                << if (link.ts_number) { >>
                                << if (link.te_inter_domain_plug_id) { >>
                                "label-restriction": [
                                    {
                                        "inclusive-exclusive": "inclusive",
                                        "index": 1,
                                        "label-end": {
                                            "te-label": {
                                                "ietf-otn-topology:tpn": << link.ts_number >>
                                            }
                                        },
                                        "label-start": {
                                            "te-label": {
                                                "ietf-otn-topology:tpn": 1
                                            }
                                        },
                                        "label-step": 1,
                                        "range-bitmap": "<< link.bitmap >>"
                                    }
                                ],
                                << } >>
                                "max-link-bandwidth": {
                                    "te-bandwidth": {
                                        "ietf-otn-topology:odulist": [
                                            {
                                                "odu-type": "ietf-otn-types:prot-ODU0",
                                                "number": << link.ts_number >>
                                            }
                                        ]
                                    }
                                },
                                "unreserved-bandwidth": [
                                    {
                                        "priority": 7,
                                        "te-bandwidth": {
                                            "ietf-otn-topology:odulist": [
                                                {
                                                    "number": << link.ts_number >>,
                                                    "odu-type": "ietf-otn-types:prot-ODU0"
                                                }
                                            ]
                                        }
                                    }
                                ]
                                << } >>
                                << if (link.eth_bandwidth) { >>
                                "max-link-bandwidth": {
                                    "te-bandwidth": {
                                        "ietf-eth-te-topology:eth-bandwidth": << link.eth_bandwidth >>
                                    }
                                },
                                "unreserved-bandwidth": [
                                    {
                                        "priority": 7,
                                        "te-bandwidth": {
                                            "ietf-eth-te-topology:eth-bandwidth": << link.eth_bandwidth >>
                                        }
                                    }
                                ]
                                << } >>
                            }
                        }
                    }
                    << }) >>
                ]
            }
            << }) >>
        ]
    }
}`;
