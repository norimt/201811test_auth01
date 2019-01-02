//
//  ViewController.swift
//  KotlinIOS
//
//  Created by m4c on 2019/01/02.
//  Copyright © 2019 cloudeleven. All rights reserved.
//

import UIKit
import shared

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        let label = UILabel(frame: CGRect(x: 0, y: 0, width: 300, height: 21))
        label.center = CGPoint(x: 160, y: 285)
        label.textAlignment = .center
        label.font = label.font.withSize(25)
        label.text = CommonExpectKt.createApplicationScreenMessage()
        view.addSubview(label)
    }


}

