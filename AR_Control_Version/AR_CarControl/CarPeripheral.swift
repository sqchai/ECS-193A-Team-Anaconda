//
//  CarPeripheral.swift
//  AR_CarControl
//
//  Created by ZhangVito on 4/27/20.
//  Copyright © 2020 Joe. All rights reserved.
//

import UIKit
import CoreBluetooth

class CarPeripheral: NSObject {

    //public static let CarUUID = CBUUID.init(string: "014069A8-FE78-3CDA-10C5-A23E92C8EBD1")
        public static let ServiceUUID = CBUUID.init(string: "49535343-FE7D-4AE5-8FA9-9FAFD205E455")
        
        public static let TXUUID = CBUUID.init(string: "49535343-1E4D-4BD9-BA61-23C647249616")
        
        public static let RXUUID = CBUUID.init(string: "49535343-8841-43F4-A8D4-ECBE34729BB3")
}
