// GoogleAutoCompletePlacePicker.m

#import "GoogleAutoCompletePlacePicker.h"
#import <React/RCTBridge.h>
#import <React/RCTUtils.h>
#import <React/RCTLog.h>

@implementation GoogleAutoCompletePlacePicker{
  GMSAutocompleteFilter *_filter;
  RCTPromiseResolveBlock _gResolve;
  RCTPromiseRejectBlock _gReject;
  UIViewController *rctPresentedViewController;
}

RCT_EXPORT_MODULE(GoogleAutoCompletePlacePicker)

RCT_EXPORT_METHOD(pickPlace: (RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
  
  _gResolve = resolve;
  _gReject = reject;
  
  dispatch_async(dispatch_get_main_queue(), ^{
    
    GMSAutocompleteViewController *acController = [[GMSAutocompleteViewController alloc] init];
      
    self->rctPresentedViewController = RCTPresentedViewController();
    acController.delegate = self;

    // Specify the place data types to return.
    GMSPlaceField fields = (GMSPlaceFieldName | GMSPlaceFieldPlaceID | GMSPlaceFieldCoordinate | GMSPlaceFieldFormattedAddress);
    acController.placeFields = fields;

    // Specify a filter.
    self->_filter = [[GMSAutocompleteFilter alloc] init];
    self->_filter.type = kGMSPlacesAutocompleteTypeFilterNoFilter;
    acController.autocompleteFilter = self->_filter;
    
    // Display the autocomplete view controller.
    
    [self->rctPresentedViewController presentViewController:acController animated:YES completion:nil];
      
  });
    
}


- (void)viewController:(nonnull GMSAutocompleteViewController *)viewController didAutocompleteWithPlace:(nonnull GMSPlace *)place {
  RCTLog(@"Place %@ ", place);
  _gResolve(@{
      @"name": [place name],
      @"placeID": [place placeID],
      @"formattedAddress": [place formattedAddress],
      @"coordinate": @{
        @"latitude" : [NSNumber numberWithDouble:place.coordinate.latitude],
        @"longitude" : [NSNumber numberWithDouble:place.coordinate.longitude]
      },
    });
  [self->rctPresentedViewController dismissViewControllerAnimated:YES completion:nil];
  
}

- (void)viewController:(nonnull GMSAutocompleteViewController *)viewController didFailAutocompleteWithError:(nonnull NSError *)error {
  [self->rctPresentedViewController dismissViewControllerAnimated:YES completion:nil];
  _gReject(@"GOOGLE_PLACE_PICKER_UNKNOWN_ERROR", [NSString stringWithFormat:@"Some unknown error occured"], error);
}

- (void)wasCancelled:(nonnull GMSAutocompleteViewController *)viewController {
  [self->rctPresentedViewController dismissViewControllerAnimated:YES completion:nil];
  _gReject(@"GOOGLE_PLACE_PICKER_CANCEL_ERROR", [NSString stringWithFormat:@"Cancelled by user"], nil);
}


@end
