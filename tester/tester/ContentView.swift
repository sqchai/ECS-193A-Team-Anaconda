//
//  ContentView.swift
//  tester
//
//  Created by Xuanchen Zhou on 1/31/20.
//  Copyright © 2020 pathac. All rights reserved.
//

import SwiftUI

struct ContentView: View {
    @State private var showDetails = false
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
            
            if showDetails {
                Text("fuck I'm dead let's do this tmr")
                    .font(.largeTitle)
            }
            
        }.frame(width: 500, height: 500)
    }
}


struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
