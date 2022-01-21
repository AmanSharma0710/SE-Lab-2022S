#include <bits/stdc++.h>
#include "rapidxml.hpp"

using namespace std;
using namespace rapidxml;

static double LARGE_DOUBLE = 1e10;
static double PI = 3.14159265358979323846;
static double RADIUS_EARTH = 6372797.56085;
//static double RADIUS_EARTH = 6371000; // for WGS84(GPS)
static double DEGREE_TO_RADIAN = PI / 180;

//Calculates haversine distance between the two points given latitude and longitude in degrees
//Returns distance in meters
//https://en.wikipedia.org/wiki/Haversine_formula
//Function inspired by: https://stackoverflow.com/questions/27126714/c-latitude-and-longitude-distance-calculator/63767823
double distance_calculator(double lat_a, double lon_a, double lat_b, double lon_b){
    double  lat_b_radians = lat_b * DEGREE_TO_RADIAN;
    double  lat_a_radians = lat_a * DEGREE_TO_RADIAN;
    double  lat_diff = (lat_a-lat_b) * DEGREE_TO_RADIAN;
    double  lng_diff = (lon_a-lon_b) * DEGREE_TO_RADIAN;

    double  a = sin(lat_diff/2) * sin(lat_diff/2) + cos(lat_b_radians) * cos(lat_a_radians) * sin(lng_diff/2) * sin(lng_diff/2);
    double  c = 2 * atan2(sqrt(a), sqrt(1-a));

    double  distance = RADIUS_EARTH * c;
    
    return distance;
}

//Class for storing the information of a node
class node{
    public:
    long long int id;
    double lat;
    double lon;
    string name;
    node(){     //Default constructor
        id = 0;
        lat = 0;
        lon = 0;
        name = "";
    }
    node(long long int id, double lat, double lon, string name){        //Constructor
        this->id = id;
        this->lat = lat;
        this->lon = lon;
        this->name = name;
    }
};

//Class for storing the information of a way
class way{
    public:
    long long int id;
    vector<long long int> nodeIDs;
    way(){      //Default constructor
        id = 0;
        nodeIDs = vector<long long int>();
    }
    way(long long int id){      //Constructor
        this->id = id;
        this->nodeIDs = vector<long long int>();
    }
};

xml_document<> doc;
xml_node<> * osm_root = NULL;
vector<vector<pair<int, double>>> adj;      //Adjacency list of the graph, stores the nodeID and the distance between the two nodes


//The nodes have unique IDs but they are not necessarily in order
//We assign consecutive whole numbers to the nodes to make adjacence list easier to work with (i.e. nodeID = 0, nodeID = 1, nodeID = 2, ...)
//The nodeIDnew can be used to access the node in the adjacency list and nodeIDnew can be used to access old nodeID from the nodes vector
//NodeIDnew from old nodeID can be found using the index_map
   
int main(){

    //name_id_map contains all the pairs of names along with their corresponding ids
    vector<pair<string, long long int>> name_id_map;
    //nodes contains information about all the nodes in the map
    vector<node> nodes;
    //ways contains information about all the ways in the map
    vector<way> ways;
    //index_map contains the index of the node in the nodes vector
    map<long long int, int> index_map;


    //Taking the input of the osm file
    string osm_file_name;
    cout << "Enter the name of the osm file: ";
    cin >> osm_file_name;
    ifstream theFile(osm_file_name);    //opening the file
    //READING AND PARSING THE OSM FILE
    cout << "Staring the parsing of the OSM file...\n" << setprecision(10);
    // Read the sample.xml file into a buffer
    vector<char> buffer((istreambuf_iterator<char>(theFile)), istreambuf_iterator<char>());
    buffer.push_back('\0');
    // Parse the buffer
    doc.parse<0>(&buffer[0]);
   

    // Find out the root node
    osm_root = doc.first_node("osm");
    // Iterate over all the child nodes of the root node and if they are nodes, increase the count of nodes and if they are ways, increase the count of ways
    for (xml_node<> * child_node = osm_root->first_node(); child_node; child_node = child_node->next_sibling()){
        if (child_node->name() == string("node")){
            //if node contains no tags, we add it to the node vector and continue
            if(!child_node->first_node("tag")){
                nodes.push_back(node(stoll(child_node->first_attribute("id")->value()), stod(child_node->first_attribute("lat")->value()), stod(child_node->first_attribute("lon")->value()), ""));
                index_map[stoll(child_node->first_attribute("id")->value())] = nodes.size()-1;
                continue;
            }

            string name = "";
            //if node contains tags, we iterate over them till we find the name tag and add it to the name_mapping
            for(xml_node<> * tag = child_node->first_node("tag"); tag; tag = tag->next_sibling()){
                if(tag->first_attribute("k")->value() == string("name")){
                    name = tag->first_attribute("v")->value();
                }
                if(tag->first_attribute("k")->value() == string("name:en")){
                    name = tag->first_attribute("v")->value();
                    break;
                }
            }

            //We add node to the nodes map while maintaining the original name there
            nodes.push_back(node(stoll(child_node->first_attribute("id")->value()), stod(child_node->first_attribute("lat")->value()), stod(child_node->first_attribute("lon")->value()), name));
            index_map[stoll(child_node->first_attribute("id")->value())] = nodes.size()-1;

            //convert name to lowercase so that we can search without case sensitivity
            transform(name.begin(), name.end(), name.begin(), ::tolower);
            //if name is not empty, we add it to the name_mapping
            if(name != ""){
                name_id_map.push_back(make_pair(name, stoll(child_node->first_attribute("id")->value())));
            }
        }
        else if (child_node->name() == string("way")){
            long long int id = stoll(child_node->first_attribute("id")->value());
            //We add way to the ways vector
            ways.push_back(way(id));
            for(xml_node<> * nd = child_node->first_node("nd"); nd; nd = nd->next_sibling()){
                if(nd->name()!=string("nd")){
                    continue;
                }
                //we add the node id to the way
                ways.back().nodeIDs.push_back(stoll(nd->first_attribute("ref")->value()));
            }
        }
    }

    //Printing the number of nodes and ways
    cout<<"Total number of nodes: "<<nodes.size()<<endl;
    cout<<"Total number of ways: "<<ways.size()<<endl;


    //CREATING THE ADJACENCY LIST
    cout<<"Creating the adjacency list...\n";
    adj.assign(nodes.size(), vector<pair<int, double>>());
    for(int i=0; i<ways.size(); i++){
        for(int j=0; j<(int)ways[i].nodeIDs.size() - 1 ; j++){
            int index1 = index_map[ways[i].nodeIDs[j]];
            int index2 = index_map[ways[i].nodeIDs[j+1]];
            double distance = distance_calculator(nodes[index_map[ways[i].nodeIDs[j]]].lat, nodes[index_map[ways[i].nodeIDs[j]]].lon, nodes[index_map[ways[i].nodeIDs[j+1]]].lat, nodes[index_map[ways[i].nodeIDs[j+1]]].lon);
            adj[index1].push_back(make_pair(index2, distance));
            adj[index2].push_back(make_pair(index1, distance));
        }
    }
    cout<<"Adjacency list created.\n";


    //The main interactive part of the program that implements the user interface
    while(true){
        cout<<"\n\n\n\nWhat do you want to do?\n0. Exit\n1. Search for a place by name\n2. Search for k closest places to a given place\n3. Find shortest path between two places\nEnter your choice: ";
        int choice;
        cin>>choice;
        if(choice<0 || choice>3){
            cout<<"Invalid choice. Please enter a valid choice.\n";
            continue;
        }

        //EXITING THE PROGRAM
        if(choice==0){
            break;
        }

        //Initiate a substring search for a place by name
        if(choice==1){
            cout<<"Enter the name you want to search for: ";
            string name;
            cin>>name;

            //convert name to lowercase so that we can search without case sensitivity
            transform(name.begin(), name.end(), name.begin(), ::tolower);
            int matches_found = 0;
            vector<long long int> matches;

            //search for the name in the name_mapping
            for(int i=0;i<name_id_map.size();i++){
                if(name_id_map[i].first.find(name) != string::npos){
                    matches_found++;
                    matches.push_back(name_id_map[i].second);
                }
            }

            //Depending on the number of matches found, we print the results
            if(matches_found == 0){
                cout<<"No matches found\n";
            }
            else{
                cout<<"Matches found: "<<matches_found<<endl;
                for(int i=0;i<matches.size();i++){
                    cout<<"Name: "<<nodes[index_map[matches[i]]].name<<endl;
                    cout<<"NodeID: "<<matches[i]<<endl;
                    cout<<"Latitude: "<<nodes[index_map[matches[i]]].lat<<endl;
                    cout<<"Longitude: "<<nodes[index_map[matches[i]]].lon<<endl;
                    cout<<"\n";
                }
            }
        }

        //Search for k closest places to a given place
        //We sort all the remaining places according to their distance from the given place and print the k closest places
        //Time complexity: O(nlog(n)) where n is the number of nodes
        //Can be reduced to O(n) by using k-th order statistic but that introduces a large overhead
        if(choice==2){
            cout<<"Enter the ID of the place you want closest points from(Enter 0 to exit): ";
            long long int id;
            cin>>id;
            while(index_map.find(id) == index_map.end() && id){
                cout<<"Invalid ID. Please enter a valid ID.\n";
                cin>>id;
            }
            if(!id){
                continue;
            }
            cout<<"Enter the number of closest points you want to find: ";
            int k;
            cin>>k;
            //Handling invalid inputs for k
            if(k<=0){
                cout<<"Invalid number of closest points. Please enter a valid number next time.\n";
                continue;
            }
            if(k>nodes.size()-1){
                cout<<"Number of closest points is greater than the number of nodes. Please enter a valid number next time.\n";
                continue;
            }

            //We sort the remaining places according to their distance from the given place
            vector<pair<double, long long int>> distances;
            for(int i=0;i<nodes.size();i++){
                if(nodes[i].id != id){
                    distances.push_back(make_pair(distance_calculator(nodes[index_map[id]].lat, nodes[index_map[id]].lon, nodes[i].lat, nodes[i].lon), nodes[i].id));
                }
            }
            sort(distances.begin(), distances.end());

            //We print the k closest places
            cout<<k<<" closest points are: "<<endl;
            for(int i=0; i<k; i++){
                cout<<i+1<<". ";
                if(nodes[index_map[distances[i].second]].name != ""){
                    cout<<"Name: "<<nodes[index_map[distances[i].second]].name<<endl;
                }
                cout<<"NodeID: "<<distances[i].second<<endl;
                cout<<"Distance from the given place: "<<distances[i].first<<"meters"<<endl;
                printf("Latitude: %.4f\n", nodes[index_map[distances[i].second]].lat);
                printf("Longitude: %.4f\n", nodes[index_map[distances[i].second]].lon);
                cout<<"\n";
            }
        }

        //Find the shortest path between two places
        //We use Uniform Cost Search to find the shortest path
        //It is a greedy algorithm that always chooses the cheapest path from the source and adds the next closest node to a priority queue
        if(choice==3){
            //We first ask the user to enter the source and destination
            cout<<"Enter the ID of the place you want to start from(Enter 0 to exit): ";
            long long int id1;
            cin>>id1;
            while(index_map.find(id1) == index_map.end() && id1){
                cout<<"Invalid ID. Please enter a valid ID.\n";
                cin>>id1;
            }
            if(!id1){
                continue;
            }
            cout<<"Enter the ID of the place you want to end at(Enter 0 to exit): ";
            long long int id2;
            cin>>id2;
            while(index_map.find(id2) == index_map.end() && id2){
                cout<<"Invalid ID. Please enter a valid ID.\n";
                cin>>id2;
            }
            if(!id2){
                continue;
            }
            //handle the case where the source and destination are the same
            if(id1 == id2){
                cout<<"Start and end points are the same.\n";
                continue;
            }

            //Run the Uniform Cost Search algorithm
            int start_node = index_map[id1], end_node = index_map[id2];
            vector<int> parent(nodes.size(), -1);           //parent[i] stores the parent of node i so that we can reconstruct the path later
            vector<double> distance(nodes.size(), LARGE_DOUBLE);        //distance[i] stores the distance from the source to node i
            priority_queue<pair<double, int>, vector<pair<double, int>>, greater<pair<double, int>>> pq;    //min-heap to store the nodes with their distance from the source
            pq.push(make_pair(0.0, start_node));
            distance[start_node] = 0.0;     //distance from the source to the source is 0
            while(!pq.empty()){
                int closest_node = pq.top().second;     //the closest node to the source from the unvisited nodes
                pq.pop();
                if(closest_node == end_node){           //if we have reached the destination, we can stop
                    break;
                }
                for(int i=0;i<adj[closest_node].size();i++){
                    //for all neighbours of the closest node, we check if the distance from the source to the neighbour is less than the distance from the source to the neighbour
                    //if it is, we update the distance from the source to the neighbour and update the parent of the neighbour
                    if(distance[adj[closest_node][i].first] > distance[closest_node] + adj[closest_node][i].second){
                        distance[adj[closest_node][i].first] = distance[closest_node] + adj[closest_node][i].second;
                        parent[adj[closest_node][i].first] = closest_node;
                        pq.push(make_pair(distance[adj[closest_node][i].first], adj[closest_node][i].first));
                    }
                }
            }

            //If the destination is not reachable from the source, we print an error message
            if(distance[end_node] == LARGE_DOUBLE){
                cout<<"No path found.\n";
            }

            //otherwise we print the path
            else{
                cout<<"Path found.\n";
                cout<<"Distance: ";
                if(distance[end_node] >= 1000){
                    printf("%.4lf km\n", distance[end_node]/1000);
                }
                else{
                    printf("%.4lf m\n", distance[end_node]);
                }
                vector<int> path;
                path.push_back(end_node);
                int curr_node = end_node;
                while(curr_node != start_node){
                    curr_node = parent[curr_node];
                    path.push_back(curr_node);
                }
                reverse(path.begin(), path.end());
                cout<<"Do you want to see the path?(y/n): ";
                char choice;
                cin>>choice;
                if(choice != 'y' && choice != 'Y'){
                    continue;
                }
                cout<<"Do you want to see details of the path?(y/n): ";
                cin>>choice;
                cout<<"Path: ";
                for(int i=0; i<path.size(); i++){
                    if(choice!='y' && choice!='Y'){
                        if(i){
                            cout<<" -> ";
                        }
                        if(nodes[path[i]].name != ""){
                            cout<<nodes[path[i]].name;
                        }
                        else{
                            cout<<nodes[path[i]].id;
                        }
                    }
                    else{
                        cout<<"NodeID: "<<nodes[path[i]].id<<endl;
                        printf("Latitude: %.4f\n", nodes[path[i]].lat);
                        printf("Longitude: %.4f\n", nodes[path[i]].lon);
                        printf("Distance from the start place: %.4lf meters\n\n", distance[path[i]]);
                    }
                }
                cout<<endl;
            }
        }
    }
    return 0;
}