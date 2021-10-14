// main index.js

import { NativeModules } from 'react-native';

const GoogleAutoCompletePlacePickerModule = NativeModules.GoogleAutoCompletePlacePicker

class GoogleAutoCompletePlacePicker {

    pickPlace = async() => {
        return new Promise((resolve, reject)=>{
            GoogleAutoCompletePlacePickerModule.pickPlace().then((result)=>{
                const placeData = {
                    name : result.name,
                    placeID : result.placeID,
                    formattedAddress : result.formattedAddress,
                    coordinate : result.coordinate
                }
                resolve(placeData)
            }).catch((err)=>{
                console.log(err.message)
                reject(err)
            })
        })
    }

}

export default new GoogleAutoCompletePlacePicker()
