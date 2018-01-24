//
//  RNShareFile.h
//  RNShareFile
//
//  Created by You on 2018/01/20
//  Copyright © 2018 Genii Services. All rights reserved.
//

#if __has_include("RCTBridgeModule.h")
	#import "RCTBridgeModule.h"
#else
	#import <React/RCTBridgeModule.h>
#endif

// import RCTConvert
#if __has_include(<React/RCTConvert.h>)
#import <React/RCTConvert.h>
#elif __has_include("RCTConvert.h")
#import "RCTConvert.h"
#else
#import "React/RCTConvert.h"   // Required when used as a Pod in a Swift project
#endif

#import <Foundation/Foundation.h>
#import <UIKit/UIDocumentInteractionController.h>
#import <UIKit/UIWindow.h>

@interface RNShareFile : NSObject <RCTBridgeModule, UIDocumentInteractionControllerDelegate>

@property (nonatomic, strong) UIDocumentInteractionController *documentInteractionController;

@end
