//
//  ContentView.swift
//  April8
//
//  Created by Xuanchen Zhou on 4/8/20.
//  Copyright © 2020 pathac. All rights reserved.
//

import SwiftUI
import RealityKit
import ARKit
import UIKit
import SceneKit

let ARDelegate = SessionDelegate()

struct ContentView : View {
    @State var settingView = false
    var body: some View {
        VStack {
            if self.settingView {
                ARViewContainer().edgesIgnoringSafeArea(.all)
            } else {
                HomeView(showDetails: $settingView)
            }
        }
        //return ARViewContainer().edgesIgnoringSafeArea(.all)
    }
}

struct HomeView: View {
    @Binding var showDetails: Bool
    var body: some View {
        VStack {
            Text("Welcome to vehicle control panel")
                .frame(maxWidth:300, maxHeight: .infinity)
                .padding(50)
                .font(.largeTitle)
            
            Button(action: {
            self.showDetails.toggle()// your action here
            }) {
                Text("START")
            }
            .frame(maxWidth:300, maxHeight: .infinity)
            
            //if showDetails {
                //Text("fuck I'm dead let's do this tmr")
                    //.font(.largeTitle)
            //}
            
        }.frame(width: 500, height: 500)
    }
}

struct ARViewContainer: UIViewRepresentable {

    func makeUIView(context: Context) -> ARView {
        guard let referenceImages = ARReferenceImage.referenceImages(inGroupNamed: "AR Resources", bundle: nil) else {
            fatalError("Missing expected asset catalog resources.")
        }
        
        let arView = ARView(frame: .zero)
        ARDelegate.set(arView: arView)
        arView.session.delegate = ARDelegate
        let config = ARWorldTrackingConfiguration()
        config.planeDetection = .vertical
        config.isLightEstimationEnabled = true
        config.detectionImages = referenceImages
        // Load the "Box" scene from the "Experience" Reality File
        //let boxAnchor = try! Experience.loadBox()
        
        // Add the box anchor to the scene
        //arView.scene.anchors.append(boxAnchor)
        
        arView.debugOptions = [ARView.DebugOptions.showAnchorGeometry]
        //arView.setupGestures()
        arView.session.run(config)
        return arView
        
    }
    
    func updateUIView(_ uiView: ARView, context: Context) {
    }
    
}

final class SessionDelegate: NSObject, ARSessionDelegate{
    var arView: ARView!
    var rootAnchor: AnchorEntity?

    func set(arView: ARView) {
      self.arView = arView
    }
    func session(_ session: ARSession, didAdd anchors: [ARAnchor]) {
        print("added anchor(s)")
        print(anchors[anchors.count-1].transform)
        
        guard let imageAnchor = anchors[anchors.count-1] as? ARImageAnchor else { return }
        //let referenceImage = imageAnchor.referenceImage
        let mtransform = imageAnchor.transform
        let box = CustomBox(color: .yellow, position: SIMD3(mtransform.columns.3.x, mtransform.columns.3.y, mtransform.columns.3.z))
        arView.scene.anchors.append(box)
        /*let plane = SCNPlane(width: referenceImage.physicalSize.width,
                             height: referenceImage.physicalSize.height)
        let planeNode = SCNNode(geometry: plane)
        planeNode.opacity = 0.25
        planeNode.eulerAngles.x = -.pi / 2
        arView.scene.addAnchor(<#T##anchor: HasAnchoring##HasAnchoring#>)*/
    }
}

/*extension ARView{
    func setupGestures() {
        let tap = UITapGestureRecognizer(target: self, action: #selector(self.handleTap(_:)))
        self.addGestureRecognizer(tap)
    }
    
    @objc func handleTap(_ sender: UITapGestureRecognizer? = nil) {
        
        guard let touchInView = sender?.location(in: self) else {
            return
        }
        
        rayCastingMethod(point: touchInView)
        
        //to find whether an entity exists at the point of contact
        let entities = self.entities(at: touchInView)
    }
    
    func rayCastingMethod(point: CGPoint) {
        
        
        guard let coordinator = self.session.delegate as? SessionDelegate else{ print("GOOD NIGHT"); return }
        
        guard let raycastQuery = self.makeRaycastQuery(from: point,
                                                       allowing: .existingPlaneInfinite,
                                                       alignment: .vertical) else {
                                                        
                                                        print("failed first")
                                                        return
        }
        
        /*guard let result = self.session.raycast(raycastQuery).first else {
            print("failed")
            return
        }*/
        
        //let transformation = Transform(matrix: result.worldTransform)
        let box = CustomBox(color: .gray,position: SIMD3(Float(point.x), Float(point.y), Float(-2)))
        //self.installGestures(.all, for: box)
        //box.generateCollisionShapes(recursive: true)
        
        
        //box.transform = transformation
        
        
        //let raycastAnchor = AnchorEntity(raycastResult: result)
        //raycastAnchor.addChild(box)
        self.scene.anchors.append(box)
    }
}*/

class CustomBox: Entity, HasModel, HasAnchoring, HasCollision {
    
    required init(color: UIColor) {
        super.init()
        self.components[ModelComponent] = ModelComponent(
            mesh: .generateBox(size: 0.1),
            materials: [SimpleMaterial(
                color: color,
                isMetallic: false)
            ]
        )
    }
    
    convenience init(color: UIColor, position: SIMD3<Float>) {
        self.init(color: color)
        self.position = position
    }
    
    required init() {
        fatalError("init() has not been implemented")
    }
}

#if DEBUG
struct ContentView_Previews : PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
#endif
