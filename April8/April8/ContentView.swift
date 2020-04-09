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
        
        let arView = ARView(frame: .zero)
        //arView.session.delegate =
        let config = ARWorldTrackingConfiguration()
        config.planeDetection = [.horizontal, .vertical]
        config.isLightEstimationEnabled = true
        
        // Load the "Box" scene from the "Experience" Reality File
        //let boxAnchor = try! Experience.loadBox()
        
        // Add the box anchor to the scene
        //arView.scene.anchors.append(boxAnchor)
        
        arView.debugOptions = [ARView.DebugOptions.showAnchorGeometry]
        arView.session.run(config)
        return arView
        
    }
    
    func updateUIView(_ uiView: ARView, context: Context) {
    }
    
}

class ARViewController: UIViewController, ARSCNViewDelegate {
    
}



#if DEBUG
struct ContentView_Previews : PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
#endif
