//
//  ViewController+ARSCNViewDelegate.swift
//  AR_CarControl
//
//  Created by ZhangVito on 4/20/20.
//  Copyright © 2020 Joe. All rights reserved.
//


import ARKit

extension ViewController: ARSCNViewDelegate, ARSessionDelegate {
    
        // MARK: - ARSCNViewDelegate
    
        ///this is called when device rendereing new frames. UpdateFocusSquare is called to update the position and transformation of the focus square.
        func renderer(_ renderer: SCNSceneRenderer, updateAtTime time: TimeInterval) {
                
                ///add a thread to update the focus square when a surface is detected.
                DispatchQueue.main.async {
                        self.updateFocusSquare(shouldDisplayFS: self.shouldDisplayFS)
                }
        }
    

      
        /// - Tag: Relocalization
        func sessionShouldAttemptRelocalization(_ session: ARSession) -> Bool {
                return true
        }
}
