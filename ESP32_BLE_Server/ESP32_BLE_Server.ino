#include <BLEDevice.h>

BLEServer* pServer;
BLECharacteristic* pCharacteristic;
int rssiValue = 0;

class MyServerCallbacks : public BLEServerCallbacks {
public:
    void onConnect(BLEServer* pServer) {
        Serial.println("Device connected");
    }

    void onDisconnect(BLEServer* pServer) {
        Serial.println("Device disconnected");
    }

    void onRSSIChange(BLEServer* pServer, int rssi) {
        Serial.printf("RSSI: %d dBm\n", rssi);
        rssiValue = rssi;
    }
};

void setup() {
    Serial.begin(115200);

    // Initialize Bluetooth
    BLEDevice::init("ESP32_BLE_Device");

    // Create the BLE Server
    pServer = BLEDevice::createServer();
    MyServerCallbacks* pCallbacks = new MyServerCallbacks();
    pServer->setCallbacks(pCallbacks);

    // Create the BLE Service
    BLEService *pService = pServer->createService(BLEUUID("180D")); // Generic Heart Rate Service

    // Create the BLE Characteristic
    pCharacteristic = pService->createCharacteristic(
                        BLEUUID("2A37"), // Heart Rate Measurement Characteristic
                        BLECharacteristic::PROPERTY_READ |
                        BLECharacteristic::PROPERTY_NOTIFY
                    );

    // Set the initial value for the characteristic
    pCharacteristic->setValue("Hello, World!");

    // Start the service
    pService->start();

    // Start advertising
    BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();
    pAdvertising->addServiceUUID(pService->getUUID());
    pAdvertising->setScanResponse(true);
    pAdvertising->setMinPreferred(0x06);
    pAdvertising->setMinPreferred(0x12);
    BLEDevice::startAdvertising();

    Serial.println("Bluetooth device is advertising...");
}

void loop() {
    // Your main code here
    // ...

    // Print the RSSI value every few seconds
    if (millis() % 5000 == 0) {
        Serial.printf("Current RSSI: %d dBm\n", rssiValue);
    }

    delay(1000);
}