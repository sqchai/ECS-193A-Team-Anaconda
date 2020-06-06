//
//  TouchInteraction.swift
//  AR_CarControl
//
//  Created by ZhangVito on 4/20/20.
//  Copyright © 2020 Joe. All rights reserved.
//

import UIKit
import ARKit
import Vision


class TouchInteraction: NSObject, UIGestureRecognizerDelegate {
    
    enum DetectionError: Error {
        case not_detected
    }
    
    /// Developer setting to translate assuming the detected plane extends infinitely.
    let translateAssumingInfinitePlane = true
    
        
    /// The scene view to hit test against when moving virtual content.
    let sceneView: ARSCNView
    
    /// A reference to the view controller.
    let viewController: ViewController
    
    ///The bool to determine if it should draw or not.
    var shouldDraw: Bool?
        
    
    
        
    init(sceneView: ARSCNView, viewController: ViewController) {
        self.sceneView = sceneView
        self.viewController = viewController
        super.init()
        
        print("interaction initialized")
        
        createPanGestureRecognizer(sceneView)
        
    }
    
    // - Tag: CreatePanGesture
    func createPanGestureRecognizer(_ sceneView: ARSCNView) {
        let panGesture = ThresholdPanGesture(target: self, action: #selector(didPan(_:)))
        panGesture.delegate = self
        sceneView.addGestureRecognizer(panGesture)
    }
    
    // MARK: - Gesture Actions
    
        
    var initialCenter = CGPoint()
    @objc
    func didPan(_ gesture: ThresholdPanGesture) {
            
            guard gesture.view != nil else {return}
            let piece = gesture.view!
    
            let translation = gesture.translation(in: piece.superview)
    
            switch gesture.state {
                case .began:
                    self.initialCenter = gesture.location(in: sceneView)
            
                case .changed:
                    
                    let newCenter = CGPoint(x: initialCenter.x + translation.x, y: initialCenter.y + translation.y)
                        
                        
                    let newPoint: DrawPoint = DrawPoint()
                        
                        
                    if let query = sceneView.raycastQuery(from: newCenter, allowing: .estimatedPlane, alignment: .horizontal) {
                                
                            guard let result = sceneView.session.raycast(query).first else {
                                        return
                            }
                                
                            newPoint.simdWorldPosition = result.worldTransform.translation
                                
                                
                            sceneView.scene.rootNode.addChildNode(newPoint)
                    } else {
                         
                            //deal with it later.
                    }
                        
            
                case .ended:
                    viewController.performTheActualDetection()
                        
                    /// Check if it has the write property
                    guard let characteristic = viewController.RXCharacteristic else {return}
                    
                    guard let peripheral = viewController.peripheral else {return}
                        
                    if characteristic.properties.contains(.writeWithoutResponse) {
                        
                        guard let value = "R,-1,50,0".data(using: .utf8) else {return}
                        peripheral.writeValue(value, for: characteristic, type: .withoutResponse)
                        
                        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                            guard let val = "M,0,50,0".data(using: .utf8) else {return}
                                peripheral.writeValue(val, for: characteristic, type: .withoutResponse)
                        }

                    }
                

                default:
                        /// Reset the current position tracking.
                        shouldDraw = true;
                }
    }
    
    
    func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldRecognizeSimultaneouslyWith otherGestureRecognizer: UIGestureRecognizer) -> Bool {
        // Allow objects to be translated and rotated at the same time.
        return true
    }
    
    
        
        
}
 


/// Extends `UIGestureRecognizer` to provide the center point resulting from multiple touches.
extension UIGestureRecognizer {
    func center(in view: UIView) -> CGPoint? {
        guard numberOfTouches > 0 else { return nil }
        
        let first = CGRect(origin: location(ofTouch: 0, in: view), size: .zero)

        let touchBounds = (1..<numberOfTouches).reduce(first) { touchBounds, index in
            return touchBounds.union(CGRect(origin: location(ofTouch: index, in: view), size: .zero))
        }

        return CGPoint(x: touchBounds.midX, y: touchBounds.midY)
    }

}

