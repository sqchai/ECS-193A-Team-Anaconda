//
//  DrawPoint.swift
//  AR_CarControl
//
//  Created by ZhangVito on 4/25/20.
//  Copyright © 2020 Joe. All rights reserved.
//

import Foundation
import ARKit

let SIZE = CGFloat(0.013)
let DEPTH = CGFloat(0.00001)


class DrawPoint: SCNNode {
    
        static var boxGeo: SCNBox?
    
        override init() {
                super.init()
        
                if DrawPoint.boxGeo == nil {
                        DrawPoint.boxGeo = SCNBox(width: SIZE, height: DEPTH, length: SIZE, chamferRadius: 0.001)
            
                        // Setup the material of the point
                        let material = DrawPoint.boxGeo!.firstMaterial
                        material?.lightingModel = SCNMaterial.LightingModel.blinn
                        material?.diffuse.contents  = UIColor.red
                        material?.normal.contents   = UIColor.red
                        material?.specular.contents = UIColor.red
                }
        
                let object = SCNNode(geometry: DrawPoint.boxGeo!)
                
                object.transform = SCNMatrix4MakeTranslation(0.0, Float(DEPTH) / 2.0, 0.0)
        
                self.addChildNode(object)
        
        }
        
        required init?(coder aDecoder: NSCoder) {
               fatalError("\(#function) has not been implemented")
        }

}
