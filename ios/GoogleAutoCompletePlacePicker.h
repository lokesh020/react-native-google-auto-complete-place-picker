// GoogleAutoCompletePlacePicker.h

#import <React/RCTBridgeModule.h>
#import <UIKit/UIKit.h>
@import GooglePlaces;

@interface GoogleAutoCompletePlacePicker : NSObject<RCTBridgeModule, GMSAutocompleteViewControllerDelegate>

@end