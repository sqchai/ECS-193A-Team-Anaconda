//
//  ViewController.swift
//  AR_CarControl
//
//  Created by WenbaiZhang on 3/19/20.
//  Copyright © 2020 Joe. All rights reserved.
//

import UIKit
import SceneKit
import ARKit
import AVFoundation
import os.signpost
import CoreBluetooth
import Vision



///The main AR screen view that we draw and make the car move. It is set to the first view to dispear as the app started for testing, but then set up a main menu.
class ViewController: UIViewController, CBPeripheralDelegate, CBCentralManagerDelegate {
    
        ///object detection section
    
        @available(iOS 12.0, *)
        private lazy var detectionRequest: VNCoreMLRequest = {
            do {
                /// Instantiate the model from its generated Swift class.
                let model = try VNCoreMLModel(for: MyObjectDetector1().model)
            
                let request = VNCoreMLRequest(model: model, completionHandler: { [weak self] request, error in self?.processDetections(for: request, error: error)
                })
                
                request.imageCropAndScaleOption = .scaleFit
                
                /// Use CPU for Vision processing , no waste of the GPU when display videos.
                request.usesCPUOnly = true
                
                return request
            } catch {
                fatalError("Failed to load Vision ML model: \(error)")
            }
        }()
        
        @available(iOS 12.0, *)
        func processDetections(for request: VNRequest, error: Error?) {
            guard let results = request.results else {
                print("Unable to detect object.\n\(error!.localizedDescription)")
                return
            }
            /// The `results` will always be `VNClassificationObservation`s, as specified by the Core ML model in this project.
            let detections = results as! [VNRecognizedObjectObservation]
            
            ///the x and y value to store the center of all results
            var xValue = 0.0;
            var yValue = 0.0;
            
            
            
            for detection in detections {
                print(detection.labels.map({"\($0.identifier) confidence: \($0.confidence)"}).joined(separator: "\n"))
                xValue += Double(1-detection.boundingBox.origin.x)
                yValue += Double(1-detection.boundingBox.origin.y)
            }
 
            
            ///get the mean of xs and ys, which should be the center of all features, center of the car.
            carX = xValue / Double(detections.count)
            carY = yValue / Double(detections.count)
            
                    
            print("x is :", carX, ". y is :", carY);
            print("The end of iteration_____________________________\n")
           
        }
        
        /// The pixel buffer used to Vision requests.
        var currentBuffer: CVPixelBuffer?
    
        ///the current frame to detect images.
        var currentFrame: ARFrame?
        
        ///the saved location of the car after the detection, and the TouchInteraction class will access to it to make the car move.
        var carX = 0.0
        var carY = 0.0
        
        
    
        ///-------------------------------------------------------------------------------------
        
        ///Bluetooth variables
        private var centralManager: CBCentralManager!
        var peripheral: CBPeripheral!
        var RXCharacteristic: CBCharacteristic?
        
        
        func centralManagerDidUpdateState(_ central: CBCentralManager) {
                print("Central state update: ")
                if central.state != .poweredOn {
                        print("Central is not powered on")
                } else {
                        print("Central scanning for devices");
                        centralManager.scanForPeripherals(withServices: nil, options: nil)
                }
        }
        
        /// Handles the result of the bluetooth scan
        func centralManager(_ central: CBCentralManager, didDiscover peripheral: CBPeripheral, advertisementData: [String : Any], rssi RSSI: NSNumber) {
                
                //get the device name.
                let device = (advertisementData as NSDictionary).object(forKey: CBAdvertisementDataLocalNameKey) as? NSString
                
                if device?.contains("HC-02") == true {
                        self.centralManager.stopScan()
                           
                        self.peripheral = peripheral
                        self.peripheral.delegate = self
                           
                        self.centralManager.connect(self.peripheral, options: nil)
                 }
                

        }
        
        
        /// The handler if we do connect succesfully
        func centralManager(_ central: CBCentralManager, didConnect peripheral: CBPeripheral) {
                if peripheral == self.peripheral {
                        print("Connected to the HC-02 device Car")
                        peripheral.discoverServices(nil)
                }
        }
        
        // Handles discovery event
        func peripheral(_ peripheral: CBPeripheral, didDiscoverServices error: Error?) {
                if let services = peripheral.services {
                        for service in services {
                                if service.uuid == CarPeripheral.ServiceUUID {
                                        print("Bluetooth Service     found")
                                        //Now kick off discovery of characteristics
                                        peripheral.discoverCharacteristics([CarPeripheral.TXUUID, CarPeripheral.RXUUID], for: service)
                        return
                    }
                }
            }
        }
        
        // Handling discovery of characteristics
        func peripheral(_ peripheral: CBPeripheral, didDiscoverCharacteristicsFor service: CBService, error: Error?) {
                if let characteristics = service.characteristics {
                        for characteristic in characteristics {
                                if characteristic.uuid == CarPeripheral.TXUUID {
                                        print("TX Pin characteristic found")
                                } else if characteristic.uuid == CarPeripheral.RXUUID {
                                        print("RX Pin characteristic found")
                                        self.RXCharacteristic = characteristic
                                }
                        }
                }
        }
        
        
        
        
        
        ///AR Configuration and startup-----------------------------------------------------------------------------------------------------------------
        
        @IBOutlet var sceneView: ARSCNView!
        
        var targetWorldMap: ARWorldMap?
        
        
        var focusSquare = FocusSquare()  ///The focus square that displays the surface.
        
        
        let coachingOverlay = ARCoachingOverlayView()  ///text overlay on top of the view to show message menu.
        
        let updateQueue = DispatchQueue(label: "objectQueue")
        
        var shouldDisplayFS = true;
        
        ///The main AR session to run.
        var session: ARSession {
               return sceneView.session
        }
        

        
        ///mannage user gesture to control drawing.
        lazy var drawControll = TouchInteraction(sceneView: sceneView, viewController: self)
        
        
        
        ///configuration as the view loaded.
        override func viewDidLoad() {
                super.viewDidLoad()
        
                /// Set the view's delegate
                sceneView.delegate = self
                
                sceneView.session.delegate = self
        
                /// Show statistics such as fps and timing information
                sceneView.showsStatistics = false
                
                
                sceneView.scene.rootNode.addChildNode(focusSquare)
        
                // Create a new scene
                //let scene = SCNScene(named: "art.scnassets/ship.scn")!
        
                // Set the scene to the view
                //sceneView.scene = scene
                
                ///configure the bluetooth settings.
                centralManager = CBCentralManager(delegate: self, queue: nil)
                
        }
        

        ///set up when the AR session is started.
        override func viewDidAppear(_ animated: Bool) {
               super.viewDidAppear(animated)
               
               /// Prevent the screen from being dimmed to avoid interuppting the AR experience.
                UIApplication.shared.isIdleTimerDisabled = true

               /// Setup the configurations------------------------------------------------------
                drawControll.shouldDraw = true
                        
                let configuration = ARWorldTrackingConfiguration()
                
                ///let the camera auto focus to find the surface in the middle content.
                configuration.isAutoFocusEnabled = true
                
                ///we only need horizontal configuration.
                configuration.planeDetection = [.horizontal]
                
                if #available(iOS 12.0, *) {
                        configuration.environmentTexturing = .automatic
                }
                
                ///start the ARSession-------------------------------------------------------------
                session.run(configuration, options: [.resetTracking, .removeExistingAnchors])
           
        }
    
        override func viewWillDisappear(_ animated: Bool) {
                super.viewWillDisappear(animated)
        
                /// Pause the view's session
                sceneView.session.pause()
        }
        
        // MARK: - ARSCNViewDelegate
        
        /*
         // Override to create and configure nodes for anchors added to the view's session.
         func renderer(_ renderer: SCNSceneRenderer, nodeFor anchor: ARAnchor) -> SCNNode? {
                let node = SCNNode()
     
                return node
         }
         */
    
    
        func sessionInterruptionEnded(_ session: ARSession) {
                // Reset tracking and/or remove existing anchors if consistent tracking is required
        
        }
        
        ///
        func updateFocusSquare(shouldDisplayFS: Bool) {
                if shouldDisplayFS  {
                        focusSquare.unhide()
                } else {
                        focusSquare.hide()
                }
            
                // Perform ray casting only when ARKit tracking is working.
                if let camera = session.currentFrame?.camera, case .normal = camera.trackingState,
                let query = sceneView.getRaycastQuery(),
                let result = sceneView.castRay(for: query).first {
                
                        updateQueue.async {
                                self.sceneView.scene.rootNode.addChildNode(self.focusSquare)
                                self.focusSquare.state = .detecting(raycastResult: result, camera: camera)
                        }
                        if !coachingOverlay.isActive {
                                //addObjectButton.isHidden = false
                        }
                                //statusViewController.cancelScheduledMessage(for: .focusSquare)
                } else {
                        updateQueue.async {
                                self.focusSquare.state = .initializing
                                self.sceneView.pointOfView?.addChildNode(self.focusSquare)
                        }
                        //addObjectButton.isHidden = true
                        //objectsViewController?.dismiss(animated: false, completion: nil)
                }
        }
        
        func displayErrorMessage(title: String, message: String) {
                //
        }
    
    
        func session(_ session: ARSession, didUpdate frame: ARFrame) {
            currentFrame = frame;
            //performTheActualDetection()
        }
    
        func performTheActualDetection() {
            guard currentBuffer == nil, case .normal = currentFrame!.camera.trackingState else {
                return
            }
                
            /// Retain the image buffer for Vision processing.
            self.currentBuffer = currentFrame!.capturedImage
            if #available(iOS 12.0, *) {
                /// Most computer vision tasks are not rotation agnostic so it is important to pass in the orientation of the image with respect to device.
                let orientation = CGImagePropertyOrientation(rawValue: UInt32(UIDevice.current.orientation.rawValue))!
                        
                let requestHandler = VNImageRequestHandler(cvPixelBuffer: currentBuffer!, orientation: orientation)
                    updateQueue.async {
                        do {
                            /// Release the pixel buffer when done, allowing the next buffer to be processed.
                            defer { self.currentBuffer = nil }
                            try requestHandler.perform([self.detectionRequest])
                        } catch {
                            print("Error: Vision request failed with error \"\(error)\"")
                        }
                    }
                } else {
                        /// Fallback on earlier versions
                }
        }
        
        
        
        
        
}
