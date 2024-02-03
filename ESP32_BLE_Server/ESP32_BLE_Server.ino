#include <BLEDevice.h>

// --------
// Constants
// --------
#define SERVICE_UUID        "25AE1441-05D3-4C5B-8281-93D4E07420CF"
#define CHAR_READ_UUID      "25AE1442-05D3-4C5B-8281-93D4E07420CF"
#define CHAR_WRITE_UUID     "25AE1443-05D3-4C5B-8281-93D4E07420CF"
#define CHAR_INDICATE_UUID  "25AE1444-05D3-4C5B-8281-93D4E07420CF"

#define CMD_HELP "help"
#define CMD_DISCONNECT "disc"
#define CMD_READ "read"
#define CMD_WRITE "write="

BLEServer* pServer;
BLECharacteristic* pReadCharacteristic;
BLECharacteristic* pWriteCharacteristic;
BLECharacteristic* pIndicateCharacteristic;
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
    BLEService *pService = pServer->createService(BLEUUID(SERVICE_UUID));

    // // Create the BLE Characteristic
    // pCharacteristic = pService->createCharacteristic(
    //                     BLEUUID("2A37"), // Heart Rate Measurement Characteristic
    //                     BLECharacteristic::PROPERTY_READ |
    //                     BLECharacteristic::PROPERTY_NOTIFY
    //                 );

        // Create a Read Characteristic
    pReadCharacteristic = pService->createCharacteristic(
                             BLEUUID(CHAR_READ_UUID),
                             BLECharacteristic::PROPERTY_READ
                         );

    // Create a Write Characteristic
    pWriteCharacteristic = pService->createCharacteristic(
                              BLEUUID(CHAR_WRITE_UUID),
                              BLECharacteristic::PROPERTY_WRITE
                          );

    // Create an Indicate Characteristic
    pIndicateCharacteristic = pService->createCharacteristic(
                                 BLEUUID(CHAR_INDICATE_UUID),
                                 BLECharacteristic::PROPERTY_INDICATE
                             );

    // Set initial values for characteristics
    pReadCharacteristic->setValue("Read Value");
    pWriteCharacteristic->setValue("Write Value");
    pIndicateCharacteristic->setValue("Indicate Value");

    // Start the service
    pService->start();

    // Start advertising
    BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();
    pAdvertising->addServiceUUID(pService->getUUID());
    pAdvertising->setScanResponse(true);
    pAdvertising->setMinPreferred(0x32); // 50 ms = 80 * 0.625 ms
    pAdvertising->setMaxPreferred(0x64); // 100 ms = 160 * 0.625 ms
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