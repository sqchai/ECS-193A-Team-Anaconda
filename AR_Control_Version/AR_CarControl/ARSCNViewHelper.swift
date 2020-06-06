//
//  ARSCNViewHelper.swift
//  AR_CarControl
//
//  Created by ZhangVito on 4/20/20.
//  Copyright © 2020 Joe. All rights reserved.
//


import Foundation
import ARKit


extension ARSCNView {
        /**
         Type conversion wrapper for original `unprojectPoint(_:)` method.
         Used in contexts where sticking to SIMD3<Float> type is helpful.
         */
        func unprojectPoint(_ point: SIMD3<Float>) -> SIMD3<Float> {
                return SIMD3<Float>(unprojectPoint(SCNVector3(point)))
        }
    
        ///When you have a raycast, you perferm it in the 3D enviroment in AR Session, if the ray hits an surface, it returns the ARRaycastResult which contains the transform, anchor and other info about the plane location and rotation.
        func castRay(for query: ARRaycastQuery) -> [ARRaycastResult] {
                return session.raycast(query)
        }

        ///find a raycast that is angled from the center of the screen to the camera direction.
        func getRaycastQuery(for alignment: ARRaycastQuery.TargetAlignment = .any) -> ARRaycastQuery? {
                return raycastQuery(from: screenCenter, allowing: .estimatedPlane, alignment: alignment)
        }
    
        ///the center of the screen to find the focus square.
        var screenCenter: CGPoint {
                return CGPoint(x: bounds.midX, y: bounds.midY)
        }
}

