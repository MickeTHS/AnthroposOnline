#Updates and additions for Blender 2.6X by Derek McPherson


bl_info = {
    "name": "MD3 (+shaders)",
    "author": "Derek McPherson, Xembie, PhaethonH, Bob Holcomb, Damien McGinnes, Robert (Tr3B) Beckebans",
    "version": (1,6,1),# March the thirty first, twenty twelve.
    "blender": (2, 6, 2),
    "api": 36991,
    "location": "File > Export > Quake Model 3 (.md3)",
    "description": "Export mesh Quake Model 3 (.md3)",
    "warning": "",
    "wiki_url": "",
    "tracker_url": "http://forums.duke4.net/topic/5358-blender-26-md3-export-script/",
    "category": "Import-Export"}

import bpy, struct, math, os, time

##### User options: Exporter default settings
default_logtype = 'overwrite' ## console, overwrite, append
default_dumpall = False 
default_triangulate = True


MAX_QPATH = 64

MD3_IDENT = "IDP3"
MD3_VERSION = 15
MD3_MAX_TAGS = 16
MD3_MAX_SURFACES = 32
MD3_MAX_FRAMES = 1024
MD3_MAX_SHADERS = 256
MD3_MAX_VERTICES = 8192    #4096
MD3_MAX_TRIANGLES = 16384  #8192  
MD3_XYZ_SCALE = 64.0



class md3Vert:
	xyz = []
	normal = 0
	binaryFormat = "<3hH"
	
	def __init__(self):
		self.xyz = [0.0, 0.0, 0.0]
		self.normal = 0
		
	def GetSize(self):
		return struct.calcsize(self.binaryFormat)
	
	# copied from PhaethonH <phaethon@linux.ucla.edu> md3.py
	def Decode(self, latlng):
		lat = (latlng >> 8) & 0xFF;
		lng = (latlng) & 0xFF;
		lat *= math.pi / 128;
		lng *= math.pi / 128;
		x = math.cos(lat) * math.sin(lng)
		y = math.sin(lat) * math.sin(lng)
		z =                 math.cos(lng)
		retval = [ x, y, z ]
		return retval
	
	# copied from PhaethonH <phaethon@linux.ucla.edu> md3.py
	def Encode(self, normal):
		x = normal[0]
		y = normal[1]
		z = normal[2]
		# normalize
		l = math.sqrt((x*x) + (y*y) + (z*z))
		if l == 0:
			return 0
		x = x/l
		y = y/l
		z = z/l
		
		if (x == 0.0) & (y == 0.0) :
			if z > 0.0:
				return 0
			else:
				return (128 << 8)
		
		lng = math.acos(z) * 255 / (2 * math.pi)
		lat = math.atan2(y, x) * 255 / (2 * math.pi)
		retval = ((int(lat) & 0xFF) << 8) | (int(lng) & 0xFF)
		return retval
		
	def Save(self, file):
		tmpData = [0] * 4
		tmpData[0] = int(self.xyz[0] * MD3_XYZ_SCALE)
		tmpData[1] = int(self.xyz[1] * MD3_XYZ_SCALE)
		tmpData[2] = int(self.xyz[2] * MD3_XYZ_SCALE)
		tmpData[3] = self.normal
		data = struct.pack(self.binaryFormat, tmpData[0], tmpData[1], tmpData[2], tmpData[3])
		file.write(data)
		
class md3TexCoord:
	u = 0.0
	v = 0.0

	binaryFormat = "<2f"

	def __init__(self):
		self.u = 0.0
		self.v = 0.0
		
	def GetSize(self):
		return struct.calcsize(self.binaryFormat)

	def Save(self, file):
		tmpData = [0] * 2
		tmpData[0] = self.u
		tmpData[1] = 1.0 - self.v
		data = struct.pack(self.binaryFormat, tmpData[0], tmpData[1])
		file.write(data)

class md3Triangle:
	indexes = []

	binaryFormat = "<3i"

	def __init__(self):
		self.indexes = [ 0, 0, 0 ]
		
	def GetSize(self):
		return struct.calcsize(self.binaryFormat)

	def Save(self, file):
		tmpData = [0] * 3
		tmpData[0] = self.indexes[0]
		tmpData[1] = self.indexes[2] # reverse
		tmpData[2] = self.indexes[1] # reverse
		data = struct.pack(self.binaryFormat,tmpData[0], tmpData[1], tmpData[2])
		file.write(data)

class md3Shader:
	name = ""
	index = 0
	
	binaryFormat = "<%dsi" % MAX_QPATH

	def __init__(self):
		self.name = ""
		self.index = 0
		
	def GetSize(self):
		return struct.calcsize(self.binaryFormat)

	def Save(self, file):
		tmpData = [0] * 2
		tmpData[0] = str.encode(self.name)
		tmpData[1] = self.index
		data = struct.pack(self.binaryFormat, tmpData[0], tmpData[1])
		file.write(data)

class md3Surface:
	ident = ""
	name = ""
	flags = 0
	numFrames = 0
	numShaders = 0
	numVerts = 0
	numTriangles = 0
	ofsTriangles = 0
	ofsShaders = 0
	ofsUV = 0
	ofsVerts = 0
	ofsEnd = 0
	shaders = []
	triangles = []
	uv = []
	verts = []
	
	binaryFormat = "<4s%ds10i" % MAX_QPATH  # 1 int, name, then 10 ints
	
	def __init__(self):
		self.ident = ""
		self.name = ""
		self.flags = 0
		self.numFrames = 0
		self.numShaders = 0
		self.numVerts = 0
		self.numTriangles = 0
		self.ofsTriangles = 0
		self.ofsShaders = 0
		self.ofsUV = 0
		self.ofsVerts = 0
		self.ofsEnd
		self.shaders = []
		self.triangles = []
		self.uv = []
		self.verts = []
		
	def GetSize(self):
		sz = struct.calcsize(self.binaryFormat)
		self.ofsTriangles = sz
		for t in self.triangles:
			sz += t.GetSize()
		self.ofsShaders = sz
		for s in self.shaders:
			sz += s.GetSize()
		self.ofsUV = sz
		for u in self.uv:
			sz += u.GetSize()
		self.ofsVerts = sz
		for v in self.verts:
			sz += v.GetSize()
		self.ofsEnd = sz
		return self.ofsEnd
	
	def Save(self, file):
		self.GetSize()
		tmpData = [0] * 12
		tmpData[0] = str.encode(self.ident)
		tmpData[1] = str.encode(self.name)
		tmpData[2] = self.flags
		tmpData[3] = self.numFrames
		tmpData[4] = self.numShaders
		tmpData[5] = self.numVerts
		tmpData[6] = self.numTriangles
		tmpData[7] = self.ofsTriangles
		tmpData[8] = self.ofsShaders
		tmpData[9] = self.ofsUV
		tmpData[10] = self.ofsVerts
		tmpData[11] = self.ofsEnd
		data = struct.pack(self.binaryFormat, tmpData[0],tmpData[1],tmpData[2],tmpData[3],tmpData[4],tmpData[5],tmpData[6],tmpData[7],tmpData[8],tmpData[9],tmpData[10],tmpData[11])
		file.write(data)

		# write the tri data
		for t in self.triangles:
			t.Save(file)

		# save the shader coordinates
		for s in self.shaders:
			s.Save(file)

		# save the uv info
		for u in self.uv:
			u.Save(file)

		# save the verts
		for v in self.verts:
			v.Save(file)

class md3Tag:
	name = ""
	origin = []
	axis = []
	
	binaryFormat="<%ds3f9f" % MAX_QPATH
	
	def __init__(self):
		self.name = ""
		self.origin = [0, 0, 0]
		self.axis = [0, 0, 0, 0, 0, 0, 0, 0, 0]
		
	def GetSize(self):
		return struct.calcsize(self.binaryFormat)
		
	def Save(self, file):
		tmpData = [0] * 13
		tmpData[0] = str.encode(self.name)
		tmpData[1] = float(self.origin[0])
		tmpData[2] = float(self.origin[1])
		tmpData[3] = float(self.origin[2])
		tmpData[4] = float(self.axis[0])
		tmpData[5] = float(self.axis[1])
		tmpData[6] = float(self.axis[2])
		tmpData[7] = float(self.axis[3])
		tmpData[8] = float(self.axis[4])
		tmpData[9] = float(self.axis[5])
		tmpData[10] = float(self.axis[6])
		tmpData[11] = float(self.axis[7])
		tmpData[12] = float(self.axis[8])
		data = struct.pack(self.binaryFormat, tmpData[0],tmpData[1],tmpData[2],tmpData[3],tmpData[4],tmpData[5],tmpData[6], tmpData[7], tmpData[8], tmpData[9], tmpData[10], tmpData[11], tmpData[12])
		file.write(data)
	
class md3Frame:
	mins = 0
	maxs = 0
	localOrigin = 0
	radius = 0.0
	name = ""
	
	binaryFormat="<3f3f3ff16s"
	
	def __init__(self):
		self.mins = [0, 0, 0]
		self.maxs = [0, 0, 0]
		self.localOrigin = [0, 0, 0]
		self.radius = 0.0
		self.name = ""
		
	def GetSize(self):
		return struct.calcsize(self.binaryFormat)

	def Save(self, file):
		tmpData = [0] * 11
		tmpData[0] = self.mins[0]
		tmpData[1] = self.mins[1]
		tmpData[2] = self.mins[2]
		tmpData[3] = self.maxs[0]
		tmpData[4] = self.maxs[1]
		tmpData[5] = self.maxs[2]
		tmpData[6] = self.localOrigin[0]
		tmpData[7] = self.localOrigin[1]
		tmpData[8] = self.localOrigin[2]
		tmpData[9] = self.radius
		tmpData[10] = str.encode("frame" + self.name)
		data = struct.pack(self.binaryFormat, tmpData[0],tmpData[1],tmpData[2],tmpData[3],tmpData[4],tmpData[5],tmpData[6],tmpData[7], tmpData[8], tmpData[9], tmpData[10])
		file.write(data)

class md3Object:
	# header structure
	ident = ""			# this is used to identify the file (must be IDP3)
	version = 0			# the version number of the file (Must be 15)
	name = ""
	flags = 0
	numFrames = 0
	numTags = 0
	numSurfaces = 0
	numSkins = 0
	ofsFrames = 0
	ofsTags = 0
	ofsSurfaces = 0
	ofsEnd = 0
	frames = []
	tags = []
	surfaces = []

	binaryFormat="<4si%ds9i" % MAX_QPATH  # little-endian (<), 17 integers (17i)

	def __init__(self):
		self.ident = 0
		self.version = 0
		self.name = ""
		self.flags = 0
		self.numFrames = 0
		self.numTags = 0
		self.numSurfaces = 0
		self.numSkins = 0
		self.ofsFrames = 0
		self.ofsTags = 0
		self.ofsSurfaces = 0
		self.ofsEnd = 0
		self.frames = []
		self.tags = []
		self.surfaces = []

	def GetSize(self):
		self.ofsFrames = struct.calcsize(self.binaryFormat)
		self.ofsTags = self.ofsFrames
		for f in self.frames:
			self.ofsTags += f.GetSize()
		self.ofsSurfaces += self.ofsTags
		for t in self.tags:
			self.ofsSurfaces += t.GetSize()
		self.ofsEnd = self.ofsSurfaces
		for s in self.surfaces:
			self.ofsEnd += s.GetSize()
		return self.ofsEnd
		
	def Save(self, file):
		self.GetSize()
		tmpData = [0] * 12
		tmpData[0] = str.encode(self.ident)
		tmpData[1] = self.version
		tmpData[2] = str.encode(self.name)
		tmpData[3] = self.flags
		tmpData[4] = self.numFrames
		tmpData[5] = self.numTags
		tmpData[6] = self.numSurfaces
		tmpData[7] = self.numSkins
		tmpData[8] = self.ofsFrames
		tmpData[9] = self.ofsTags
		tmpData[10] = self.ofsSurfaces
		tmpData[11] = self.ofsEnd

		data = struct.pack(self.binaryFormat, tmpData[0],tmpData[1],tmpData[2],tmpData[3],tmpData[4],tmpData[5],tmpData[6],tmpData[7], tmpData[8], tmpData[9], tmpData[10], tmpData[11])
		file.write(data)

		for f in self.frames:
			f.Save(file)
			
		for t in self.tags:
			t.Save(file)
			
		for s in self.surfaces:
			s.Save(file)


def message(log,msg):
  if log:
    log.write(msg + "\n")
  else:
    print(msg)

class md3Settings:
  def __init__(self,
               savepath,
               name,
               logtype,
               dumpall=False,
               triangulate=False,
               scale=1.0,
               offsetx=0.0,
               offsety=0.0,
               offsetz=0.0):
    self.savepath = savepath
    self.name = name
    self.logtype = logtype
    self.dumpall = dumpall
    self.triangulate = triangulate
    self.scale = scale
    self.offsetx = offsetx
    self.offsety = offsety
    self.offsetz = offsetz

def print_md3(log,md3,dumpall):
  message(log,"Header Information")
  message(log,"Ident: " + str(md3.ident))
  message(log,"Version: " + str(md3.version))
  message(log,"Name: " + md3.name)
  message(log,"Flags: " + str(md3.flags))
  message(log,"Number of Frames: " + str(md3.numFrames))
  message(log,"Number of Tags: " + str(md3.numTags))
  message(log,"Number of Surfaces: " + str(md3.numSurfaces))
  message(log,"Number of Skins: " + str(md3.numSkins))
  message(log,"Offset Frames: " + str(md3.ofsFrames))
  message(log,"Offset Tags: " + str(md3.ofsTags))
  message(log,"Offset Surfaces: " + str(md3.ofsSurfaces))
  message(log,"Offset end: " + str(md3.ofsEnd))
  
  if dumpall:
    message(log,"Frames:")
    for f in md3.frames:
      message(log," Mins: " + str(f.mins[0]) + " " + str(f.mins[1]) + " " + str(f.mins[2]))
      message(log," Maxs: " + str(f.maxs[0]) + " " + str(f.maxs[1]) + " " + str(f.maxs[2]))
      message(log," Origin(local): " + str(f.localOrigin[0]) + " " + str(f.localOrigin[1]) + " " + str(f.localOrigin[2]))
      message(log," Radius: " + str(f.radius))
      message(log," Name: " + f.name)

    message(log,"Tags:")
    for t in md3.tags:
      message(log," Name: " + t.name)
      message(log," Origin: " + str(t.origin[0]) + " " + str(t.origin[1]) + " " + str(t.origin[2]))
      message(log," Axis[0]: " + str(t.axis[0]) + " " + str(t.axis[1]) + " " + str(t.axis[2]))
      message(log," Axis[1]: " + str(t.axis[3]) + " " + str(t.axis[4]) + " " + str(t.axis[5]))
      message(log," Axis[2]: " + str(t.axis[6]) + " " + str(t.axis[7]) + " " + str(t.axis[8]))

    message(log,"Surfaces:")
    for s in md3.surfaces:
      message(log," Ident: " + s.ident)
      message(log," Name: " + s.name)
      message(log," Flags: " + str(s.flags))
      message(log," # of Frames: " + str(s.numFrames))
      message(log," # of Shaders: " + str(s.numShaders))
      message(log," # of Verts: " + str(s.numVerts))
      message(log," # of Triangles: " + str(s.numTriangles))
      message(log," Offset Triangles: " + str(s.ofsTriangles))
      message(log," Offset UVs: " + str(s.ofsUV))
      message(log," Offset Verts: " + str(s.ofsVerts))
      message(log," Offset End: " + str(s.ofsEnd))
      message(log," Shaders:")
      for shader in s.shaders:
        message(log,"  Name: " + shader.name)
        message(log,"  Index: " + str(shader.index))
      message(log," Triangles:")
      for tri in s.triangles:
        message(log,"  Indexes: " + str(tri.indexes[0]) + " " + str(tri.indexes[1]) + " " + str(tri.indexes[2]))
      message(log," UVs:")
      for uv in s.uv:
        message(log,"  U: " + str(uv.u))
        message(log,"  V: " + str(uv.v)) 
      message(log," Verts:")
      for vert in s.verts:
        message(log,"  XYZ: " + str(vert.xyz[0]) + " " + str(vert.xyz[1]) + " " + str(vert.xyz[2]))
        message(log,"  Normal: " + str(vert.normal))

  shader_count = 0
  vert_count = 0
  tri_count = 0
  for surface in md3.surfaces:
    shader_count += surface.numShaders
    tri_count += surface.numTriangles
    vert_count += surface.numVerts
    if surface.numShaders >= MD3_MAX_SHADERS:
      message(log,"!Warning: Shader limit (" + str(surface.numShaders) + "/" + str(MD3_MAX_SHADERS) + ") reached for surface " + surface.name)
    if surface.numVerts >= MD3_MAX_VERTICES:
      message(log,"!Warning: Vertex limit (" + str(surface.numVerts) + "/" + str(MD3_MAX_VERTICES) + ") reached for surface " + surface.name)
    if surface.numTriangles >= MD3_MAX_TRIANGLES:
      message(log,"!Warning: Triangle limit (" + str(surface.numTriangles) + "/" + str(MD3_MAX_TRIANGLES) + ") reached for surface " + surface.name)
  
  if md3.numTags >= MD3_MAX_TAGS:
    message(log,"!Warning: Tag limit (" + str(md3.numTags) + "/" + str(MD3_MAX_TAGS) + ") reached for md3!")
  if md3.numSurfaces >= MD3_MAX_SURFACES:
    message(log,"!Warning: Surface limit (" + str(md3.numSurfaces) + "/" + str(MD3_MAX_SURFACES) + ") reached for md3!")
  if md3.numFrames >= MD3_MAX_FRAMES:
    message(log,"!Warning: Frame limit (" + str(md3.numFrames) + "/" + str(MD3_MAX_FRAMES) + ") reached for md3!")
    
  message(log,"Total Shaders: " + str(shader_count))
  message(log,"Total Triangles: " + str(tri_count))
  message(log,"Total Vertices: " + str(vert_count))

def save_md3(settings):###################### MAIN BODY     
  starttime = time.clock()#start timer
  newlogpath = os.path.splitext(settings.savepath)[0] + ".log"
  dumpall = settings.dumpall
  if settings.logtype == "append":
    log = open(newlogpath,"a")
  elif settings.logtype == "overwrite":
    log = open(newlogpath,"w")
  else:
    log = 0
  message(log,"######################BEGIN######################")
  bpy.ops.object.mode_set(mode='OBJECT')
  md3 = md3Object()
  md3.ident = MD3_IDENT
  md3.version = MD3_VERSION
  md3.name = settings.name
  md3.numFrames = (bpy.context.scene.frame_end + 1) - bpy.context.scene.frame_start
  global actobject
  global convert_to_tris
  actobject = bpy.context.scene.objects.active
  selobjects = bpy.context.selected_objects

######Find scale value for fitting very small objects to md3 world space
  scale_md3 = True
  if settings.scale != 1:
    scale_md3 = False #Allows manual scaling to override auto scaling
  scene_maxs = [0, 0, 0]
  if scale_md3 == True:
    for obj in selobjects:
      if obj.type == 'MESH':
        obj_maxs = [0] * 3
        for frame in range(bpy.context.scene.frame_start,bpy.context.scene.frame_end + 1):
          bpy.context.scene.frame_set(frame)
          for i in range(0,3):
            if obj.dimensions[i] == 0:
              scale_md3 = False #Cancel if any object has an axis dimension of 0 (2D Objects)
            obj_maxs[i] = round(max(obj_maxs[i],obj.dimensions[i]),5)          
          if dumpall: message(log,"Object bounds for"+str(frame)+str(obj.dimensions))
        if dumpall: message(log,"Object maxs"+str(obj_maxs))
        scene_maxs = max(scene_maxs,obj_maxs)
      if dumpall: message(log,"Selected objects maxs"+str(scene_maxs))
    if scale_md3 == True:
      scene_minimum = min(scene_maxs[0],scene_maxs[1],scene_maxs[2])
      scene_maximum = max(scene_maxs[0],scene_maxs[1],scene_maxs[2])
      if dumpall: message(log,"Selected objects min single axis dimension "+str(scene_minimum))
      if dumpall: message(log,"Selected objects max single axis dimension "+str(scene_maximum))
      if scene_minimum < 25:
        my_scale = round(25/scene_minimum,2)
        if scene_maximum * my_scale > 750:#Selected Objects bounding box ratio for auto scale is
                        # 1 min axis dimension to 30 max axis dimension
          scale_md3 = False #Cancel if autoscaling makes any object too big (??750??)
      else: scale_md3 = False #for objects large enough not to need scaling   
    if scale_md3 == True:
      settings.scale = my_scale      
      message(log,"Scaling export by a value of " + str(my_scale) + " to fit MD3 space")

####### Convert to MD3 
  for obj in selobjects:
    if obj.type == 'MESH':
      convert_to_tris = False      
      bpy.context.scene.frame_set(bpy.context.scene.frame_start)

      for face in obj.data.faces:
        if (len(face.vertices) > 3) & settings.triangulate == True:
          convert_to_tris = True                             
      if convert_to_tris == True:
        me_SaveMesh = obj.data.copy()      
        scene = bpy.context.scene      
        scene.objects.active = obj
        bpy.ops.object.mode_set(mode='EDIT')
        bpy.ops.mesh.select_all(action='SELECT')
        bpy.ops.mesh.quads_convert_to_tris()
        bpy.ops.object.mode_set(mode='OBJECT')
        scene.objects.active = actobject      
        message(log,"Converted quads in UV map of " + obj.name + " to tris.")
        message(log,"Exporting UV texture coordinates for " + obj.name)
        message(log,"Exporting " + obj.name)
        nobj = obj.to_mesh(bpy.context.scene, True, 'PREVIEW')
        obj.data = me_SaveMesh
        me_SaveMesh = []        
      else:
        message(log,"Exporting UV texture coordinates for " + obj.name)
        message(log,"Exporting " + obj.name)
        nobj = obj.to_mesh(bpy.context.scene, True, 'PREVIEW')
     
      UVImage = nobj.uv_textures[0] # ERROR: An object needs to be unwrapped. 
      texCoords = UVImage.data
      nsurface = md3Surface() 
      nsurface.name = obj.name
      nsurface.ident = MD3_IDENT
      nshader = md3Shader()
      #Add only 1 shader per surface/object
      try:
        #Using custom properties allows a longer string
        nshader.name = obj["md3shader"]#Set Property Value to shader path/filename
      except:
        if obj.active_material:      
          nshader.name = obj.active_material.name
        else:
          nshader.name = "NULL"      
      nsurface.shaders.append(nshader)
      nsurface.numShaders = 1
 
      vertlist = []
      myInt = 0
      for f,face in enumerate(nobj.faces):
        faceTexCoords = texCoords[myInt] 
        myInt = myInt + 1 
        ntri = md3Triangle()

        if len(face.vertices) != 3:
          message(log,"Found a nontriangle face in object " + obj.name)
          continue

        for v,vert_index in enumerate(face.vertices):
          uv_u = round(faceTexCoords.uv[v][0],5)
          uv_v = round(faceTexCoords.uv[v][1],5)
          match = 0
          match_index = 0
          for i,vi in enumerate(vertlist):
            if vi == vert_index:
              if nsurface.uv[i].u == uv_u and nsurface.uv[i].v == uv_v:
                match = 1
                match_index = i

          if match == 0:
            vertlist.append(vert_index)
            ntri.indexes[v] = nsurface.numVerts
            ntex = md3TexCoord()
            ntex.u = uv_u
            ntex.v = uv_v
            nsurface.uv.append(ntex)
            nsurface.numVerts += 1
          else:
            ntri.indexes[v] = match_index
        nsurface.triangles.append(ntri)
        nsurface.numTriangles += 1
    
      for frame in range(bpy.context.scene.frame_start,bpy.context.scene.frame_end + 1):
        bpy.context.scene.frame_set(frame)
        for face in obj.data.faces:
          if (len(face.vertices) > 3) & settings.triangulate == True:
            convert_to_tris = True                             
        if convert_to_tris == True:
          me_SaveMesh = obj.data.copy()      
          scene = bpy.context.scene      
          scene.objects.active = obj
          bpy.ops.object.mode_set(mode='EDIT')
          bpy.ops.mesh.select_all(action='SELECT')
          bpy.ops.mesh.quads_convert_to_tris()
          bpy.ops.object.mode_set(mode='OBJECT')
          scene.objects.active = actobject      
          if dumpall:message(log,"Converted quads in frame " + str(frame) + " of " + obj.name + " to tris.")
          if dumpall:message(log,"Exporting frame " + str(frame) + " of " + obj.name)
          fobj = obj.to_mesh(bpy.context.scene, True, 'PREVIEW')
          obj.data = me_SaveMesh
          me_SaveMesh = []        
        else:
          if dumpall:message(log,"Exporting frame " + str(frame) + " of " + obj.name)
          fobj = obj.to_mesh(bpy.context.scene, True, 'PREVIEW')        
        nframe = md3Frame()
        nframe.name = str(frame)
        ## Apply location data from objects and armatures
        if obj.parent == "True":
          if obj.parent.name == "Armature":
            if obj.find_armature() != NULL:
              skel_loc = obj.parent.location      
              nframe.localOrigin = obj.location - skel_loc
              my_matrix = obj.matrix_world * obj.matrix_parent_inverse
        else:
          nframe.localOrigin = obj.location
          my_matrix = obj.matrix_world

        ## Locate, sort, encode verts and normals   
        for vi in vertlist:
          vert = fobj.vertices[vi]
          nvert = md3Vert()
          nvert.xyz = my_matrix * vert.co
          nvert.xyz[0] = round((nvert.xyz[0] * settings.scale) + settings.offsetx,5)
          nvert.xyz[1] = round((nvert.xyz[1] * settings.scale) + settings.offsety,5)
          nvert.xyz[2] = round((nvert.xyz[2] * settings.scale) + settings.offsetz,5)
          nvert.normal = nvert.Encode(vert.normal)
          ## mins, maxs, radius... count frames and surfaces
          for i in range(0,3):
            nframe.mins[i] = min(nframe.mins[i],nvert.xyz[i])
            nframe.maxs[i] = max(nframe.maxs[i],nvert.xyz[i])
          minlength = math.sqrt(math.pow(nframe.mins[0],2) + math.pow(nframe.mins[1],2) + math.pow(nframe.mins[2],2))
          maxlength = math.sqrt(math.pow(nframe.maxs[0],2) + math.pow(nframe.maxs[1],2) + math.pow(nframe.maxs[2],2))
          nframe.radius = round(max(minlength,maxlength),5)
          nsurface.verts.append(nvert) 
        md3.frames.append(nframe)
        nsurface.numFrames += 1
        bpy.data.meshes.remove(fobj)
      md3.surfaces.append(nsurface)
      md3.numSurfaces += 1
      bpy.data.meshes.remove(nobj)
      obj = []

    elif obj.type == 'EMPTY':# I think this is all wrong (the matrix locations)
      md3.numTags += 1
      for frame in range(bpy.context.scene.frame_start,bpy.context.scene.frame_end + 1):
        bpy.context.scene.frame_set(frame)     
        ntag = md3Tag()
        ntag.name = obj.name
        ntag.origin[0] = round((obj.matrix_world[3][0] * settings.scale) + settings.offsetx,5)
        ntag.origin[1] = round((obj.matrix_world[3][1] * settings.scale) + settings.offsety,5)
        ntag.origin[2] = round((obj.matrix_world[3][2] * settings.scale) + settings.offsetz,5)
        ntag.axis[0] = obj.matrix_world[0][0]
        ntag.axis[1] = obj.matrix_world[0][1]
        ntag.axis[2] = obj.matrix_world[0][2]
        ntag.axis[3] = obj.matrix_world[1][0]
        ntag.axis[4] = obj.matrix_world[1][1]
        ntag.axis[5] = obj.matrix_world[1][2]
        ntag.axis[6] = obj.matrix_world[2][0]
        ntag.axis[7] = obj.matrix_world[2][1]
        ntag.axis[8] = obj.matrix_world[2][2]
        md3.tags.append(ntag)
  
  if bpy.context.selected_objects:
    file = open(settings.savepath, "wb")
    md3.Save(file)
    bpy.context.scene.frame_set(bpy.context.scene.frame_start)
    print_md3(log,md3,settings.dumpall)
    file.close()
    message(log,"MD3 saved to " + settings.savepath)
    elapsedtime = round(time.clock() - starttime,5)
    message(log,"Elapsed " + str(elapsedtime) + " seconds")
    if scale_md3 == True:
      message(log,"Scaled export by a value of " + str(my_scale) + " to fit MD3 space")      
  else:
    message(log,"Select an object to export!")
    
  if log:
    print("Logged to",newlogpath)
    log.close()

from bpy.props import *

class ExportMD3(bpy.types.Operator):
  '''Export to .md3'''
  bl_idname = "export.md3"
  bl_label = 'Export MD3'
    
  logenum = [("console","Console","log to console"),
             ("append","Append","append to log file"),
             ("overwrite","Overwrite","overwrite log file")]

  filepath = StringProperty(subtype = 'FILE_PATH',name="File Path", description="Filepath for exporting", maxlen= 1024, default="")
  md3name = StringProperty(name="MD3 Name", description="MD3 header name / skin path (64 bytes)",maxlen=64,default="")
  md3logtype = EnumProperty(name="Save log", items=logenum, description="File logging options",default =str(default_logtype))
  md3dumpall = BoolProperty(name="Dump all", description="Dump all data for md3 to log",default=default_dumpall)
  md3triangulate = BoolProperty(name="Triangulate", description="Triangulate mesh during export",default=default_triangulate)
  md3scale = FloatProperty(name="Manual Scale", description="Manually scale all objects from world origin (0,0,0) Overrides auto scaling",default=1.0,precision=5)
  md3offsetx = FloatProperty(name="Offset X", description="Transition scene along x axis",default=0.0,precision=5)
  md3offsety = FloatProperty(name="Offset Y", description="Transition scene along y axis",default=0.0,precision=5)
  md3offsetz = FloatProperty(name="Offset Z", description="Transition scene along z axis",default=0.0,precision=5)

  def execute(self, context):
   settings = md3Settings(savepath = self.properties.filepath,
                          name = self.properties.md3name,
                          logtype = self.properties.md3logtype,
                          dumpall = self.properties.md3dumpall,
                          triangulate = self.properties.md3triangulate,
                          scale = self.properties.md3scale,
                          offsetx = self.properties.md3offsetx,
                          offsety = self.properties.md3offsety,
                          offsetz = self.properties.md3offsetz)
   save_md3(settings)
   return {'FINISHED'}

  def invoke(self, context, event):
    wm = context.window_manager
    wm.fileselect_add(self)
    return {'RUNNING_MODAL'}

  @classmethod
  def poll(cls, context):
    return context.active_object != None

def menu_func(self, context):
  self.layout.operator(ExportMD3.bl_idname, text="MD3 (+shaders)", icon='BLENDER')
  
def register():
  bpy.utils.register_class(ExportMD3)
  bpy.types.INFO_MT_file_export.append(menu_func)

def unregister():
  bpy.utils.unregister_class(ExportMD3)
  bpy.types.INFO_MT_file_export.remove(menu_func)

if __name__ == "__main__":
  register()
